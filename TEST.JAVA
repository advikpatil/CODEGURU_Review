package com.epsilon.agilityevents.custom.evaluator;
 
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.core.rule.VariableRestriction.VariableContextEntry;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
 

public class StringIgnoreCaseEvaluatorDefinition implements EvaluatorDefinition {
 
    private static final Logger logger = LogManager.getLogger(StringIgnoreCaseEvaluatorDefinition.class);
 
    protected static final String equalIgnoreCaseOperator = com.epsilon.agilityevents.drools.operators.Operator.EQ_IGNORECASE.getDroolsOperator();
    protected static final String inEqualIgnoreCaseOperator = com.epsilon.agilityevents.drools.operators.Operator.IN_IGNORECASE.getDroolsOperator();
    protected static final String containsIgnoreCaseOperator = com.epsilon.agilityevents.drools.operators.Operator.CONTAINS_IGNORECASE.getDroolsOperator();
    protected static final String strStartWithIgnoreCaseOperator = com.epsilon.agilityevents.drools.operators.Operator.STARTSWITH_IGNORECASE.getDroolsOperator();
    protected static final String strEndWithIgnoreCaseOperator = com.epsilon.agilityevents.drools.operators.Operator.ENDSWITH_IGNORECASE.getDroolsOperator();
 
    
    public static Operator EQUALSIGNORECASE_COMPARE;
    public static Operator NOT_EQUALSIGNORECASE_COMPARE;
 
    public static Operator INEQUALSIGNORECASE_COMPARE;
    public static Operator NOT_INEQUALSIGNORECASE_COMPARE;
 
    public static Operator CONTAINSIGNORECASE_COMPARE;
    public static Operator NOT_CONTAINSIGNORECASE_COMPARE;
    
    public static Operator STRSTARTSWITHIGNORECASE_COMPARE;
    public static Operator STRENDSWITHIGNORECASE_COMPARE;
    
    private static String[] SUPPORTED_IDS;
 
    private Evaluator[] evaluator;
 
    {
        init();
    }
 
    static void init() {
        if (Operator.determineOperator(equalIgnoreCaseOperator, false) == null) {
            EQUALSIGNORECASE_COMPARE = Operator.addOperatorToRegistry(equalIgnoreCaseOperator, false);
            NOT_EQUALSIGNORECASE_COMPARE = Operator.addOperatorToRegistry(equalIgnoreCaseOperator, true);
        }
 
        if (Operator.determineOperator(inEqualIgnoreCaseOperator, false) == null) {
            INEQUALSIGNORECASE_COMPARE = Operator.addOperatorToRegistry(inEqualIgnoreCaseOperator, false);
            NOT_INEQUALSIGNORECASE_COMPARE = Operator.addOperatorToRegistry(inEqualIgnoreCaseOperator, true);
        }
        
        if (Operator.determineOperator(containsIgnoreCaseOperator, false) == null) {
            CONTAINSIGNORECASE_COMPARE = Operator.addOperatorToRegistry(containsIgnoreCaseOperator, false);
            NOT_CONTAINSIGNORECASE_COMPARE = Operator.addOperatorToRegistry(containsIgnoreCaseOperator, true);
        }
 
        if (Operator.determineOperator(strStartWithIgnoreCaseOperator, false) == null) {
            STRSTARTSWITHIGNORECASE_COMPARE = Operator.addOperatorToRegistry(strStartWithIgnoreCaseOperator, false);
        }
        
        if (Operator.determineOperator(strEndWithIgnoreCaseOperator, false) == null) {
            STRENDSWITHIGNORECASE_COMPARE = Operator.addOperatorToRegistry(strEndWithIgnoreCaseOperator, false);
        }
        
        if (SUPPORTED_IDS == null) {
            SUPPORTED_IDS = new String[] { equalIgnoreCaseOperator, inEqualIgnoreCaseOperator, containsIgnoreCaseOperator, strStartWithIgnoreCaseOperator, strEndWithIgnoreCaseOperator };
        }
    }
 
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(evaluator);
 
    }
 
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        evaluator = (Evaluator[]) in.readObject();
    }
 
    @Override
    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }
 
    @Override
    public boolean isNegatable() {
        return true;
    }
 
    @Override
    public Evaluator getEvaluator(@SuppressWarnings("rawtypes") ValueType type, String operatorId, boolean isNegated, String parameterText, Target leftTarget, Target rightTarget) {
 
        com.epsilon.agilityevents.drools.operators.Operator customOperator = com.epsilon.agilityevents.drools.operators.Operator.findDroolsOperator(operatorId);
 
        Evaluator result = null;
        switch (customOperator) {
            case EQ_IGNORECASE:
                result = new StringEqualsIgnoreCaseEvaluator(type, isNegated);
                break;
            case IN_IGNORECASE:
                result = new StringInEqualsIgnoreCaseEvaluator(type, isNegated);
                break;
            case CONTAINS_IGNORECASE:
                result = new StringContainsIgnoreCaseEvaluator(type, isNegated);
                break;
            case STARTSWITH_IGNORECASE:
                result = new StringStartsWithEndsWithIgnoreCaseEvaluator(type, STRSTARTSWITHIGNORECASE_COMPARE);
                break;
            case ENDSWITH_IGNORECASE:
                result = new StringStartsWithEndsWithIgnoreCaseEvaluator(type, STRENDSWITHIGNORECASE_COMPARE);
                break;
            default:
                logger.warn("Not a valid equalsIgnoreCase operator");
        }
        return result;
    }
 
    @Override
    public Evaluator getEvaluator(@SuppressWarnings("rawtypes") ValueType type, String operatorId, boolean isNegated, String parameterText) {
        return getEvaluator(type, operatorId, isNegated, parameterText,
                Target.FACT, Target.FACT);
    }
 
    @Override
    public Evaluator getEvaluator(@SuppressWarnings("rawtypes") ValueType type, Operator operator, String parameterText) {
        return this.getEvaluator(type, operator.getOperatorString(), operator
                .isNegated(), parameterText);
    }
 
    @Override
    public Evaluator getEvaluator(@SuppressWarnings("rawtypes") ValueType type, Operator operator) {
        return this.getEvaluator(type, operator.getOperatorString(), operator
                .isNegated(), null);
    }
 
    @Override
    public boolean supportsType(@SuppressWarnings("rawtypes") ValueType type) {
        return true;
    }
 
    @Override
    public Target getTarget() {
        return Target.FACT;
    }
 
    public static class StringEqualsIgnoreCaseEvaluator extends BaseEvaluator {
 
        public StringEqualsIgnoreCaseEvaluator() {
        }
 
        public StringEqualsIgnoreCaseEvaluator(@SuppressWarnings("rawtypes") final ValueType type, final boolean isNegated) {
            super(type, isNegated ? NOT_EQUALSIGNORECASE_COMPARE : EQUALSIGNORECASE_COMPARE);
        }
 
        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor extractor, InternalFactHandle factHandle, FieldValue value) {
            final Object objectValue = extractor.getValue(workingMemory, factHandle.getObject());
            if (this.getOperator().isNegated()) {
                return !(((String) objectValue).equalsIgnoreCase((String) value.getValue()));
            }
            return (((String) objectValue).equalsIgnoreCase((String) value.getValue()));
        }
 
        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, InternalFactHandle left, InternalReadAccessor rightExtractor, InternalFactHandle right) {
            final Object value1 = leftExtractor.getValue(workingMemory, left.getObject());
            final Object value2 = rightExtractor.getValue(workingMemory, right.getObject());
            if (this.getOperator().isNegated()) {
                return !(((String) value1).equalsIgnoreCase((String) value2));
            }
            return (((String) value1).equalsIgnoreCase((String) value2));
        }
 
        @Override
        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle right) {
            if (this.getOperator().isNegated()) {
                return !(((String) right.getObject()).equalsIgnoreCase((String) ((ObjectVariableContextEntry) context).left));
            }
            return (((String) right.getObject()).equalsIgnoreCase((String) ((ObjectVariableContextEntry) context).left));
 
        }
 
        @Override
        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle left) {
            if (this.getOperator().isNegated()) {
                return !(((String) left.getObject()).equalsIgnoreCase((String) ((ObjectVariableContextEntry) context).left));
            }
            return (((String) left.getObject()).equalsIgnoreCase((String) ((ObjectVariableContextEntry) context).left));
        }
 
        @Override
        public String toString() {
            return "DroolsStringEqualsIgnoreCaseOperator equalsIgnoreCase";
        }
    }
 
    public static class StringInEqualsIgnoreCaseEvaluator extends BaseEvaluator {
 
        private com.epsilon.agilityevents.drools.operators.Operator droolsOperator;
 
        public StringInEqualsIgnoreCaseEvaluator() {
        }
 
        public StringInEqualsIgnoreCaseEvaluator(@SuppressWarnings("rawtypes") final ValueType type, final boolean isNegated) {
            super(type, isNegated ? NOT_INEQUALSIGNORECASE_COMPARE : INEQUALSIGNORECASE_COMPARE);
        }
 
        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor extractor, InternalFactHandle factHandle, FieldValue value) {
            final Object objectValue = extractor.getValue(workingMemory, factHandle.getObject());
            if (this.getOperator().isNegated()) {
                return (buildArrayList(value.getValue())).stream().noneMatch(((String) objectValue)::equalsIgnoreCase);
            }
            return (buildArrayList(value.getValue())).stream().anyMatch(((String) objectValue)::equalsIgnoreCase);
        }
 
        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, InternalFactHandle left, InternalReadAccessor rightExtractor, InternalFactHandle right) {
            final Object value1 = leftExtractor.getValue(workingMemory, left.getObject());
            final Object value2 = rightExtractor.getValue(workingMemory, right.getObject());
            if (this.getOperator().isNegated()) {
                return (buildArrayList(value2)).stream().noneMatch(((String) value1)::equalsIgnoreCase);
            }
            return (buildArrayList(value2)).stream().anyMatch(((String) value1)::equalsIgnoreCase);
        }
 
        @Override
        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle right) {
            if (this.getOperator().isNegated()) {
                return (buildArrayList(((ObjectVariableContextEntry) context).left)).stream().noneMatch(((String) right.getObject())::equalsIgnoreCase);
            }
            return (buildArrayList(((ObjectVariableContextEntry) context).left)).stream().anyMatch(((String) right.getObject())::equalsIgnoreCase);
        }
 
        @Override
        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle left) {
            if (this.getOperator().isNegated()) {
                return (buildArrayList(((ObjectVariableContextEntry) context).left)).stream().noneMatch(((String) left.getObject())::equalsIgnoreCase);
            }
            return (buildArrayList(((ObjectVariableContextEntry) context).left)).stream().anyMatch(((String) left.getObject())::equalsIgnoreCase);
        }
 
        public com.epsilon.agilityevents.drools.operators.Operator getDroolsOperator() {
            return droolsOperator;
        }
 
        public void setDroolsOperator(com.epsilon.agilityevents.drools.operators.Operator droolsOperator) {
            this.droolsOperator = droolsOperator;
        }
 
        private List<String> buildArrayList(Object input) {
            String commaSepartedString = (String) input;
            return Arrays.asList(commaSepartedString.replace("'", "").replace("(", "").replace(")", "").split("\\s*,\\s*"));
        }
 
        @Override
        public String toString() {
            return "DroolsStringEqualsIgnoreCaseOperator inEqualsIgnoreCase";
        }
    }
    
    public static class StringContainsIgnoreCaseEvaluator extends BaseEvaluator {
 
        private com.epsilon.agilityevents.drools.operators.Operator droolsOperator;
 
        public StringContainsIgnoreCaseEvaluator() {
        }
 
        public StringContainsIgnoreCaseEvaluator(@SuppressWarnings("rawtypes") final ValueType type, final boolean isNegated) {
            super(type, isNegated ? NOT_CONTAINSIGNORECASE_COMPARE : CONTAINSIGNORECASE_COMPARE);
        }
        
        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor extractor, InternalFactHandle factHandle, FieldValue value) {
            final Object objectValue = extractor.getValue(workingMemory, factHandle.getObject());
            if (this.getOperator().isNegated()) {
                return convertAsList(objectValue).stream().noneMatch(((String)value.getValue())::equalsIgnoreCase); 
            }
            return convertAsList(objectValue).stream().anyMatch(((String)value.getValue())::equalsIgnoreCase);        
        }
 
        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, InternalFactHandle left, InternalReadAccessor rightExtractor, InternalFactHandle right) {
            final Object value1 = leftExtractor.getValue(workingMemory, left.getObject());
            final Object value2 = rightExtractor.getValue(workingMemory, right.getObject());
            if (this.getOperator().isNegated()) {               
                return convertAsList(value1).stream().noneMatch(((String)value2)::equalsIgnoreCase); 
            }          
            return convertAsList(value1).stream().anyMatch(((String)value2)::equalsIgnoreCase);
        }
 
        @Override
        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle right) {
            if (this.getOperator().isNegated()) {               
                return convertAsList(right.getObject()).stream().noneMatch(((String)((ObjectVariableContextEntry) context).left)::equalsIgnoreCase); 
            }           
            return convertAsList(right.getObject()).stream().anyMatch(((String)((ObjectVariableContextEntry) context).left)::equalsIgnoreCase);
        }
 
        @Override
        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle left) {
            if (this.getOperator().isNegated()) {               
                return convertAsList(left.getObject()).stream().noneMatch(((String)((ObjectVariableContextEntry) context).left)::equalsIgnoreCase); 
            }           
            return convertAsList(left.getObject()).stream().anyMatch(((String)((ObjectVariableContextEntry) context).left)::equalsIgnoreCase);
        }
 
        public com.epsilon.agilityevents.drools.operators.Operator getDroolsOperator() {
            return droolsOperator;
        }
 
        public void setDroolsOperator(com.epsilon.agilityevents.drools.operators.Operator droolsOperator) {
            this.droolsOperator = droolsOperator;
        }
        
        private List<String> convertAsList(Object input) {
            if (input instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> result = (List<String>) input;
                return result;
            }
            throw new RuntimeException("List datatype is supported for containsIgnoreCase Operator");
        }
        
        @Override
        public String toString() {
            return "DroolsContainsIgnoreCaseOperator containsIgnoreCase";
        }     
    }
    
    public static class StringStartsWithEndsWithIgnoreCaseEvaluator extends BaseEvaluator {
 
        public StringStartsWithEndsWithIgnoreCaseEvaluator() {
        }
 
        public StringStartsWithEndsWithIgnoreCaseEvaluator(@SuppressWarnings("rawtypes") final ValueType type, final Operator operator) {
            super(type, operator);
        }
 
        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor extractor, InternalFactHandle factHandle, FieldValue value) {
            final Object objectValue = extractor.getValue(workingMemory, factHandle.getObject());    
            if(this.getOperator() == STRENDSWITHIGNORECASE_COMPARE) {
                return (((String) objectValue).toUpperCase().endsWith(((String) value.getValue()).toUpperCase()));
            }
            return (((String) objectValue).toUpperCase().startsWith(((String) value.getValue()).toUpperCase()));
        }
 
        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, InternalFactHandle left, InternalReadAccessor rightExtractor, InternalFactHandle right) {
            final Object value1 = leftExtractor.getValue(workingMemory, left.getObject());
            final Object value2 = rightExtractor.getValue(workingMemory, right.getObject());     
            if(this.getOperator() == STRENDSWITHIGNORECASE_COMPARE) {
                return (((String) value1).toUpperCase().endsWith(((String) value2).toUpperCase()));
            }
            return (((String) value1).toUpperCase().startsWith(((String) value2).toUpperCase()));
        }
 
        @Override
        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle right) {     
            if(this.getOperator() == STRENDSWITHIGNORECASE_COMPARE) {
                return (((String) right.getObject()).toUpperCase().endsWith(((String) ((ObjectVariableContextEntry) context).left).toUpperCase()));
            }
            return (((String) right.getObject()).toUpperCase().startsWith(((String) ((ObjectVariableContextEntry) context).left).toUpperCase()));
        }
 
        @Override
        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory, VariableContextEntry context, InternalFactHandle left) {
            if(this.getOperator() == STRENDSWITHIGNORECASE_COMPARE) {
                return (((String) left.getObject()).toUpperCase().equalsIgnoreCase(((String) ((ObjectVariableContextEntry) context).left).toUpperCase()));
            }
            return (((String) left.getObject()).toUpperCase().startsWith(((String) ((ObjectVariableContextEntry) context).left).toUpperCase()));
        }
 
        @Override
        public String toString() {
            return "DroolsStringStartsWithEqualsIgnoreCaseOperator strStartsWithEqualsIgnoreCase";
        }
    }
}


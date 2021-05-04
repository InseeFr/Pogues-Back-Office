package fr.insee.pogues.utils.vtl;

public class AnalyserResult {

    private String simpleMessage;
    private boolean valid;

    public AnalyserResult(String simpleMessage, boolean valid) {
        this.simpleMessage = simpleMessage;
        this.valid = valid;
    }

    public String getSimpleMessage() {
        return simpleMessage;
    }

    public void setSimpleMessage(String simpleMessage) {
        this.simpleMessage = simpleMessage;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "AnalyserResult{" +
                "simpleMessage='" + simpleMessage + '\'' +
                ", valid=" + valid +
                '}';
    }
}

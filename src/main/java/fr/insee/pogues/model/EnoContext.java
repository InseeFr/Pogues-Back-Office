package fr.insee.pogues.model;

import lombok.Getter;

/**
 * <p>Eno's context of generation.
 * A context determines a specific set of parameters in Eno.</p>
 * <p><em>Note: this notion of "business/household" contexts is an Insee concept that is currently hard-coded in many
 * services of the survey channel. In Pogues/Eno, this concept is used to trigger specific set of parameters.
 * This should be generalised in a way that does not adhere to Insee internal concepts.</em></p>
 * */
@Getter
public enum EnoContext {

    /**
     * @deprecated The default context should not be used anymore. If a generation doesn't need context,
     * simply don't provide a context in the Eno request. */
    @Deprecated
    DEFAULT("default"),

    /** Eno "business" generation context. */
    BUSINESS("business"),

    /** Eno "household" generation context. */
    HOUSEHOLD("household");

    /** Requests use a lower caps value. */
    private final String value;

    EnoContext(String value) {
        this.value = value;
    }
}

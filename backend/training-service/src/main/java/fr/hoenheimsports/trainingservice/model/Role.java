package fr.hoenheimsports.trainingservice.model;

/**
 * Represents the role a coach can have in relation to a team.
 * 
 * <p>This enum defines the different levels of responsibility a coach can have
 * when associated with a team.</p>
 * 
 * @since 1.0
 */
public enum Role {
    /**
     * Represents the main coach of a team, with primary responsibility.
     */
    MAIN, 

    /**
     * Represents an assistant coach who supports the main coach.
     */
    ASSISTANT, 

    /**
     * Represents a support staff member who provides additional assistance.
     */
    SUPPORT_STAFF
}

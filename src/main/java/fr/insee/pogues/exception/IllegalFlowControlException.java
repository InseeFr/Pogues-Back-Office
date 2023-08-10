package fr.insee.pogues.exception;

import lombok.Getter;
import lombok.Setter;

public class IllegalFlowControlException extends Exception {

    public IllegalFlowControlException(String message) {
        super(message);
    }

	public static class PoguesClientException extends Exception{

		/**
		 *
		 */
		private static final long serialVersionUID = -6576322059017435882L;

		@Getter
		@Setter
		private int status;

		@Getter @Setter
		private String details;

		/**
		 *
		 * @param status
		 * @param message
		 * @param details
		 */
		public PoguesClientException(int status, String message, String details) {
			super(message);
			this.status = status;
			this.details = details;
		}

	}
}

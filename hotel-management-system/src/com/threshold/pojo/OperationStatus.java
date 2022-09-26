package com.threshold.pojo;

public class OperationStatus {
		private boolean success;
		private String msg;

		public OperationStatus(boolean success, String msg) {
			super();
			this.success = success;
			this.msg = msg;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		@Override
		public String toString() {
			return "OperationStatus [success=" + success + ", msg=" + msg + "]";
		}

}

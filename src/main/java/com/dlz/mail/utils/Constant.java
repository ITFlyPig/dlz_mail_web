package com.dlz.mail.utils;

public class Constant {
	public static int ERROR_CODE = -1;//规定所有的方法返回-1表示不正常的执行

	/**
	 * 邮件应该被怎么处理
	 */
	public interface MailShouleStatus{
		int TIMER = 1;//定时
		int SEND = 2;//发送
		int ABANDON = 3;//抛弃

	}

	public interface SQL{
		String GET_TASKS = "select * from mail where id = ?";//获取邮件任务
		String GET_SEND_MAIL = "select * from send_mail";//获取邮件发送者的邮件账户配置
		String GET_TASK_BY_ID = "select * from mail where id = ?";//据id查询对应的邮件任务
		String UPDATE_TASK_STATUS = "update mail set status = ? where id = ?";//更新邮件任务的状态
		String GET_WAIT_HANDLE_SQL_TASK = "select * from mail where status = ?";//获取到处理的sql任务
	}

	public interface FileConfig{
		String CSV_DIR = "/files/csv";
		String CONF_DIR = "/conf";
	}

	/**
	 * 邮件任务的状态
	 * 邮件任务的状态  0：新建  1：正在执行  2：执行成功  3：执行失败  4：待发送  5：发送成成功   6：发送失败 7：sql查询已定时  8：邮件发送已定时 9:修改更新 10:丢弃
	 */
	public interface EmailStatus{
		int NEW = 0;
		int EXECUTE_ING = 1;
		int EXECUTE_SUCCESS = 2;
		int EXECUTE_FAIL = 3;
		int WAIT_SEND = 4;
		int SEND_SUCCESS = 5;
		int SEND_FAIL = 6;
		int SQL_EXCUTE_TIMER = 7;
		int EMAIL_SEND_TIMER = 8;
		int UPDATED = 9;
		int ABANDON = 10;

	}

	/**
	 * 文件的名称
	 */
	public interface FileNames{
		String SQL_MONITOR_FILE = "execute_sql.properties";//监视sql任务变化的文件
	}

	public interface Key{
		String EMAIL_TASK_ID = "email_task_id";
		String TASK_NAME = "task_name";
	}

	/**
	 * sql查询的结果放在邮件的哪里
	 */
	public interface SQL_RESULT_TYPE{
		int CONTENT = 0;//放在邮件的内容
		int ATTACMENT = 1;//放在邮件的附件中
	}

	/**
	 * 加密和解密的key
	 */
	public static final String EncryptDecryptKEY = "e852ec0ff77250be497389d2f5a1818c18bb66106b9905c4ee26fe0d256eb3b77e0ce9a28a84e4b67e4332ba37ec3aa7518148e3a682318c0fc34c391f45c201";
}

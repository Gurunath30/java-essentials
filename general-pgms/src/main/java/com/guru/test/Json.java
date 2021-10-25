package com.guru.test;

import com.guru.util.Utility;

public class Json {
	public static void main(String[] args) throws Exception {
		System.out.println(Utility.getJson("{uid=C4EB6C6A46A6E5C449C548281B68AE0B, name=Notification For Project, code=notification_for_project, type={uid=internal}, subject=Workflow : {{req.fromUserName}} {{req.actionName}} {{req.project}} project to {{req.toForwardName}}, body=<p>Workflow : {{req.stage}} {{req.fromUserName}} {{req.actionName}} {{req.project}} project to {{req.toForwardName}} on {{req.createdTime}}</p>\r\n" + 
				"<p></p>\r\n" + 
				"<p>Comments : {{req.comment}}</p>, page={uid=brd:project:list}, email_enable=true, sms_enable=false, email_provider={lable=SMTP, value=smtp, type=SMTP}, email_sender={lable=Zero Code, value=smtp_test}, email_template={lable=Emails For Project, value=emails_for_project, uid=43601EADBB834DB90C124CBD9B2EA6BA}, sms_provider=null, sms_sender=null, sms_template=null, template_type=Existing, email_body=null, email_subject=null, sms_body=null, sms_subject=null, sms_template_type=null, sms_data={}, email_data={provider={lable=SMTP, value=smtp, type=SMTP}, sender={lable=Zero Code, value=smtp_test}, template={lable=Emails For Project, value=emails_for_project, uid=43601EADBB834DB90C124CBD9B2EA6BA}}, application={uid=brd}, module={uid=project}, success=true}"));
	}
}

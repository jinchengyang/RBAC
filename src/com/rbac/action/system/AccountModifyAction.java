/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.rbac.action.system;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.rbac.common.BaseAction;
import com.rbac.entity.SysAccount;
import com.rbac.form.system.AccountModifyForm;
import com.rbac.service.AccountService;
import com.rbac.util.CommonUtils;
import com.rbac.util.PasswordHash;

/** 
 * MyEclipse Struts
 * Creation date: 04-21-2014
 * 
 * XDoclet definition:
 * @struts.action path="/accountModify" name="accountModifyForm" input="/system/accountModify.jsp" scope="request" validate="true"
 * @struts.action-forward name="success" path="/system/accountList.jsp"
 */
public class AccountModifyAction extends BaseAction {

	/** 
	 * Method execute
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		AccountModifyForm accountModifyForm = (AccountModifyForm) form;
		AccountService accountService = (AccountService) super.getBean("accountService");
		if(CommonUtils.isNotBlank(accountModifyForm.getSubmit())){
			SysAccount account = new SysAccount();
			if(CommonUtils.isNotBlank(accountModifyForm.getId())){
				Long id = CommonUtils.parseLong(accountModifyForm.getId());
				account = accountService.getAccountById(id);
				account.setModifierId(super.getCurrentAccountId(request));
				//就算在mysql中加了current stamp on update，也需要配置hibernate后才能正常运行
				account.setModifyTime(new Date());
			}
			else{
				account.setCreatorId(super.getCurrentAccountId(request));
				account.setCreateTime(new Date());
				account.setModifierId(super.getCurrentAccountId(request));
				account.setModifyTime(new Date());
			}
			account.setUsername(accountModifyForm.getUsername());
			account.setRealname(accountModifyForm.getRealname());
			account.setRoleIds(accountModifyForm.getRoleId());
			if(CommonUtils.isNotBlank(accountModifyForm.getPassword())){
				try {
					String pwdHash = PasswordHash.createHash(accountModifyForm.getPassword().trim());
					if(CommonUtils.isNotBlank(pwdHash)){
						String[] pwd = pwdHash.split(":");
						if(pwd!=null && pwd.length==3){
							account.setPassword(pwd[2]);
							account.setSalt(pwd[1]);
						}
					}
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
			}
			accountService.saveOrUpdateAccount(account);
			return mapping.findForward("success");
		}
		return mapping.getInputForward();
	}
}
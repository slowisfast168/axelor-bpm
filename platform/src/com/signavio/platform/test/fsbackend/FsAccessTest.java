/*******************************************************************************
 * Signavio Core Components
 * Copyright (C) 2012  Signavio GmbH
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.signavio.platform.test.fsbackend;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.junit.Test;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.account.business.FsAccountManager;
import com.signavio.platform.exceptions.AccountInActiveException;
import com.signavio.platform.exceptions.IncorrectPasswordException;
import com.signavio.platform.exceptions.PrincipalException;
import com.signavio.platform.exceptions.TenantException;
import com.signavio.platform.exceptions.TenantInActiveException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsRootObject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.platform.tenant.business.FsTenantManager;
import com.signavio.usermanagement.business.FsRoleManager;
import com.signavio.usermanagement.user.business.FsUser;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.FsModel;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


public class FsAccessTest {

	@Test
	public void testEditor(){
		WebResource resource = Client.create()
		        .resource("http://localhost:8080/axelor-demo/ws/rest/com.axelor.wkf.db.Editor/619");
		ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
		        .type( MediaType.APPLICATION_JSON )
		        
		        .get( ClientResponse.class );
		String res = response.getEntity(String.class);
//		String res;
//		try {
//			res = InetAddress.getLocalHost().getHostAddress();
//			System.out.println(res);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		
	}
	
//	@Test
	public void testRestApi(){
		WebResource resource = Client.create()
		        .resource("http://localhost:8080/axelor-demo/ws/rest/com.axelor.flow.db.AxelorFlow");
		
		try {
			JSONObject ji = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("model", "test");
			data.put("xmlData", "<xml></xml>");
			data.put("id", 133);	
			ji.put("operationType", "update");
			ji.put("data", data);
			ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
			        .type( MediaType.APPLICATION_JSON )
			        .entity(ji.toString())
			        .post( ClientResponse.class );

		} catch (ClientHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UniformInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
//	@Test
	public void buildStructure() throws TenantInActiveException, AccountInActiveException, PrincipalException, IncorrectPasswordException, TenantException{
		// ROOT Level
		FsAccessToken token = FsSecurityManager.createToken("root", "root", null);
		FsRootObject root = FsRootObject.getRootObject(token);
		FsAccountManager accountManager = root.getAccountManager();
		FsTenantManager tenantManager = root.getTenantManager();
		
		Assert.assertEquals(accountManager.getChildren(FsAccount.class).size(), 1);
		Assert.assertEquals(tenantManager.getChildren(FsTenant.class).size(), 1);
		
		// Tenant Level
		FsTenant onlyTenant = tenantManager.getChildren(FsTenant.class).iterator().next();
		
		FsRoleManager roleManagerForTenant = FsRoleManager.getTenantManagerInstance(FsRoleManager.class, onlyTenant, token);
		FsEntityManager entityManagerForTenant = FsEntityManager.getTenantManagerInstance(FsEntityManager.class, onlyTenant, token);
		
		Assert.assertEquals(roleManagerForTenant.getChildren(FsUser.class).size(), 1);
		
		// Test Users and Groups..
		FsUser onlyUser = roleManagerForTenant.getChildren(FsUser.class).iterator().next();

		// Test Entities..
		FsDirectory d = entityManagerForTenant.getTenantRootDirectory();
		
		visitDir(d);
//		String uuidOfModel = "";
//		for (Directory c : d.getChildDirectories()) {
//			if (c.getName().equals("B")) {
//				Model m =c.createModel("NAME", "DESCRIPTION", "TYPE", "JSON", "SVG", "BLUB");
//				uuidOfModel = m.getId();
//				c.setName("BXX");
//			}
//		}
//		
//		Model loadedModel =  SecurityManager.getInstance().loadObject(Model.class, uuidOfModel, token);
//		loadedModel.setDescription("DESCRIPTION NEW");
//		loadedModel.setName("NAME X");
//		
//		System.out.println("\nSecond Iteration:\n----------------");
//		visitDir(d);
		
	}
	
	private void visitDir(FsDirectory d) {
		System.out.println("Children of "+ d.getName()+ " :");
		for (FsModel m : d.getChildModels()){
			System.out.println(" " + m.getName());
		}
		System.out.println("ChildDirectories of "+ d.getName()+ " :");
		for (FsDirectory c : d.getChildDirectories()) {
			System.out.println(" " + c.getName() + " - " + c.getDescription());
		}
		System.out.println("");
		for (FsDirectory c : d.getChildDirectories()) {
			visitDir(c);
		}
	}
}


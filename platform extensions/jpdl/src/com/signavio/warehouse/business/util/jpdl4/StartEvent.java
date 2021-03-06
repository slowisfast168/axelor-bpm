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
package com.signavio.warehouse.business.util.jpdl4;

import java.io.StringWriter;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class StartEvent extends Node {

	public StartEvent(JSONObject startEvent) {

		this.name = JsonToJpdl.getAttribute(startEvent, "name");
		this.bounds = JsonToJpdl.getBounds(startEvent);
		this.outgoings = JsonToJpdl.getOutgoings(startEvent);
		
		this.bounds.setUlx(this.bounds.getUlx() - 9);
		this.bounds.setUly(this.bounds.getUly() - 9);
		this.bounds.setWidth(48);
		this.bounds.setHeight(48);
		
	}
	
	public StartEvent(org.w3c.dom.Node startEvent) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = startEvent.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
		
		this.bounds.setUlx(this.bounds.getUlx() + 9);
		this.bounds.setUly(this.bounds.getUly() + 9);
		this.bounds.setWidth(30);
		this.bounds.setHeight(30);
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("  <start");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid Start Event. Bounds is missing.");
		}

		if (outgoings.size() > 0) {
			jpdl.write(" >\n");
			for (Transition t : outgoings) {
				jpdl.write(t.toJpdl());
			}
			jpdl.write("  </start>\n\n");
		} else {
			jpdl.write(" />\n\n");
		}

		return jpdl.toString();
	}

	@Override
	public JSONObject toJson() throws JSONException {

		JSONObject stencil = new JSONObject();
		stencil.put("id", "StartEvent");

		JSONArray outgoing = JpdlToJson.getTransitions(outgoings);

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffff");
		if (name != null)
			properties.put("name", name);

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}
}

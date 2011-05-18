/*
 * Copyright (C) 2009 JavaRosa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.openrosa.client.jr.core.model.data;

import java.io.IOException;
import java.util.Date;

import org.openrosa.client.java.io.DataInputStream;
import org.openrosa.client.java.io.DataOutputStream;
import org.openrosa.client.jr.core.model.utils.DateUtils;
import org.openrosa.client.jr.core.util.externalizable.DeserializationException;
import org.openrosa.client.jr.core.util.externalizable.ExtUtil;
import org.openrosa.client.jr.core.util.externalizable.PrototypeFactory;

/**
 * A response to a question requesting a Date Value
 * @author Drew Roos
 *
 */
public class DateData implements IAnswerData {
	Date d;
	
	/**
	 * Empty Constructor, necessary for dynamic construction during deserialization.
	 * Shouldn't be used otherwise.
	 */
	public DateData() {
		
	}
	
	public DateData (Date d) {
		setValue(d);
	}
	
	public IAnswerData clone () {
		return new DateData(new Date(d.getTime()));
	}
	
	public void setValue (Object o) {
		//Should not ever be possible to set this to a null value
		if(o == null) {
			throw new NullPointerException("Attempt to set an IAnswerData class to null.");
		}
		d = new Date(((Date)o).getTime());
	}
	
	public Object getValue () {
		return new Date(d.getTime());
	}
	
	public String getDisplayText () {
		return DateUtils.formatDate(d, DateUtils.FORMAT_HUMAN_READABLE_SHORT);
	}

	/* (non-Javadoc)
	 * @see org.javarosa.core.services.storage.utilities.Externalizable#readExternal(java.io.DataInputStream)
	 */
	public void readExternal(DataInputStream in, PrototypeFactory pf) throws IOException, DeserializationException {
		setValue(ExtUtil.readDate(in));
	}

	/* (non-Javadoc)
	 * @see org.javarosa.core.services.storage.utilities.Externalizable#writeExternal(java.io.DataOutputStream)
	 */
	public void writeExternal(DataOutputStream out) throws IOException {
		ExtUtil.writeDate(out, d);
	}
}
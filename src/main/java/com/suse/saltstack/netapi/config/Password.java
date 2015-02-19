package com.suse.saltstack.netapi.config;

import java.lang.reflect.Array;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Simple password pojo to hold password value
 * @see http://securesoftware.blogspot.cz/2009/01/java-security-why-not-to-use-string.html
 * 
 * @author Bsarac
 *
 */
public class Password {

	private char[] password;

	private Password() {
		// hide default constructor
	}

	public Password(char[] password) {
		this.password = password;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Password) {

			Password other = Password.class.cast(obj);
			if (!ArrayUtils.isEmpty(other.getValue()) && !ArrayUtils.isEmpty(getValue())) {

				if (other.getValue().length == getValue().length) {
					for (int i = 0; i < password.length; i++) {
						if (other.getValue()[i] != password[i]) {
							return false;
						}
					}

					return true;
				}
			}

		}

		return false;
	}

	/**
	 * 
	 * Returns the password value
	 * @return {@link Array} of char ({@link Character})
	 */
	public char[] getValue() {
		return password;
	}
}

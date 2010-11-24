/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.system;

import ish.oncourse.model.College;

/**
 *
 * @author marek
 */
public interface ICollegeService {

	College findBySecurityCode(String securityCode);
	
}

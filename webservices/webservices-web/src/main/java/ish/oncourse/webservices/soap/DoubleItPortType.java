/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap;

import javax.jws.WebService;
import javax.jws.WebMethod;

@WebService
public interface DoubleItPortType {
   public int doubleIt(int numberToDouble);
}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.2-hudson-jaxb-ri-2.2-63- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.29 at 06:14:22 AM CEST 
//


package it.incalza.myhome.input.controller.configuration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for actionComand.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="actionComand">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ON"/>
 *     &lt;enumeration value="OFF"/>
 *     &lt;enumeration value="NORTH_WEST"/>
 *     &lt;enumeration value="NORTH_EAST"/>
 *     &lt;enumeration value="NORTH"/>
 *     &lt;enumeration value="SOUTH_WEST"/>
 *     &lt;enumeration value="SOUTH_EAST"/>
 *     &lt;enumeration value="SOUTH"/>
 *     &lt;enumeration value="EAST"/>
 *     &lt;enumeration value="WEST"/>
 *     &lt;enumeration value="BUTTON_0"/>
 *     &lt;enumeration value="BUTTON_1"/>
 *     &lt;enumeration value="BUTTON_2"/>
 *     &lt;enumeration value="BUTTON_3"/>
 *     &lt;enumeration value="BUTTON_4"/>
 *     &lt;enumeration value="BUTTON_5"/>
 *     &lt;enumeration value="BUTTON_6"/>
 *     &lt;enumeration value="BUTTON_7"/>
 *     &lt;enumeration value="BUTTON_8"/>
 *     &lt;enumeration value="BUTTON_9"/>
 *     &lt;enumeration value="BUTTON_10"/>
 *     &lt;enumeration value="BUTTON_11"/>
 *     &lt;enumeration value="BUTTON_12"/>
 *     &lt;enumeration value="BUTTON_13"/>
 *     &lt;enumeration value="BUTTON_14"/>
 *     &lt;enumeration value="BUTTON_15"/>
 *     &lt;enumeration value="BUTTON_16"/>
 *     &lt;enumeration value="BUTTON_17"/>
 *     &lt;enumeration value="BUTTON_18"/>
 *     &lt;enumeration value="BUTTON_19"/>
 *     &lt;enumeration value="BUTTON_20"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "actionComand")
@XmlEnum
public enum ActionComand {

    ON,
    OFF,
    NORTH_WEST,
    NORTH_EAST,
    NORTH,
    SOUTH_WEST,
    SOUTH_EAST,
    SOUTH,
    EAST,
    WEST,
    BUTTON_0,
    BUTTON_1,
    BUTTON_2,
    BUTTON_3,
    BUTTON_4,
    BUTTON_5,
    BUTTON_6,
    BUTTON_7,
    BUTTON_8,
    BUTTON_9,
    BUTTON_10,
    BUTTON_11,
    BUTTON_12,
    BUTTON_13,
    BUTTON_14,
    BUTTON_15,
    BUTTON_16,
    BUTTON_17,
    BUTTON_18,
    BUTTON_19,
    BUTTON_20;

    public String value() {
        return name();
    }

    public static ActionComand fromValue(String v) {
        return valueOf(v);
    }

}

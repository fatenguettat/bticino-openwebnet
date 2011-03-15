//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.2-hudson-jaxb-ri-2.2-63- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.15 at 06:56:21 PM CET 
//


package it.incalza.myhome.input.controller.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for command complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="command">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="OpenWebNetComands">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="OpenWebNetComand" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="SpecialComand" type="{http://www.incalza.it/myhome/input/controller/configuration}specialComand"/>
 *         &lt;/sequence>
 *       &lt;/choice>
 *       &lt;attribute name="actionComand" type="{http://www.incalza.it/myhome/input/controller/configuration}actionComand" />
 *       &lt;attribute name="room" type="{http://www.incalza.it/myhome/input/controller/configuration}room" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "command", propOrder = {
    "openWebNetComands",
    "specialComand"
})
public class Command {

    @XmlElement(name = "OpenWebNetComands")
    protected Command.OpenWebNetComands openWebNetComands;
    @XmlElement(name = "SpecialComand")
    protected SpecialComand specialComand;
    @XmlAttribute(name = "actionComand")
    protected ActionComand actionComand;
    @XmlAttribute(name = "room")
    protected Room room;

    /**
     * Gets the value of the openWebNetComands property.
     * 
     * @return
     *     possible object is
     *     {@link Command.OpenWebNetComands }
     *     
     */
    public Command.OpenWebNetComands getOpenWebNetComands() {
        return openWebNetComands;
    }

    /**
     * Sets the value of the openWebNetComands property.
     * 
     * @param value
     *     allowed object is
     *     {@link Command.OpenWebNetComands }
     *     
     */
    public void setOpenWebNetComands(Command.OpenWebNetComands value) {
        this.openWebNetComands = value;
    }

    /**
     * Gets the value of the specialComand property.
     * 
     * @return
     *     possible object is
     *     {@link SpecialComand }
     *     
     */
    public SpecialComand getSpecialComand() {
        return specialComand;
    }

    /**
     * Sets the value of the specialComand property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpecialComand }
     *     
     */
    public void setSpecialComand(SpecialComand value) {
        this.specialComand = value;
    }

    /**
     * Gets the value of the actionComand property.
     * 
     * @return
     *     possible object is
     *     {@link ActionComand }
     *     
     */
    public ActionComand getActionComand() {
        return actionComand;
    }

    /**
     * Sets the value of the actionComand property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionComand }
     *     
     */
    public void setActionComand(ActionComand value) {
        this.actionComand = value;
    }

    /**
     * Gets the value of the room property.
     * 
     * @return
     *     possible object is
     *     {@link Room }
     *     
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Sets the value of the room property.
     * 
     * @param value
     *     allowed object is
     *     {@link Room }
     *     
     */
    public void setRoom(Room value) {
        this.room = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="OpenWebNetComand" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "openWebNetComand"
    })
    public static class OpenWebNetComands {

        @XmlElement(name = "OpenWebNetComand", required = true)
        protected List<String> openWebNetComand;

        /**
         * Gets the value of the openWebNetComand property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the openWebNetComand property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOpenWebNetComand().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getOpenWebNetComand() {
            if (openWebNetComand == null) {
                openWebNetComand = new ArrayList<String>();
            }
            return this.openWebNetComand;
        }

    }

}

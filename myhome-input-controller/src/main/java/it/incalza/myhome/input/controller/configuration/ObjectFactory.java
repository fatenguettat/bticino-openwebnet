//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.2-hudson-jaxb-ri-2.2-63- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.05 at 04:15:36 AM CET 
//


package it.incalza.myhome.input.controller.configuration;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.incalza.myhome.input.controller.configuration package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.incalza.myhome.input.controller.configuration
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Command }
     * 
     */
    public Command createCommand() {
        return new Command();
    }

    /**
     * Create an instance of {@link ConfigurationCommands }
     * 
     */
    public ConfigurationCommands createConfigurationCommands() {
        return new ConfigurationCommands();
    }

    /**
     * Create an instance of {@link Commands }
     * 
     */
    public Commands createCommands() {
        return new Commands();
    }

    /**
     * Create an instance of {@link Command.OpenWebNetComands }
     * 
     */
    public Command.OpenWebNetComands createCommandOpenWebNetComands() {
        return new Command.OpenWebNetComands();
    }

}

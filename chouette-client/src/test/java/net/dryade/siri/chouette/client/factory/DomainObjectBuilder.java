/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.factory;


public class DomainObjectBuilder {
    public static DomainObjectBuilder aNew() {
        return new DomainObjectBuilder();
    }

    public InfoMessageBuilder infoMessage() {
        return InfoMessageBuilder.create();
    }
}

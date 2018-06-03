package org.frekele.fiscal.focus.nfe.client.model.nfe.request.body;

import org.frekele.fiscal.focus.nfe.client.core.FocusNFeEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author frekele - Leandro Kersting de Freitas
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NFeCCeBodyRequest implements FocusNFeEntity {

    private static final long serialVersionUID = 1L;

    private String correcao;

    public NFeCCeBodyRequest() {
        super();
    }
}

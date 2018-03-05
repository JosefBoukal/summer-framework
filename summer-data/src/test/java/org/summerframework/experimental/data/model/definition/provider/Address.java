package org.summerframework.experimental.data.model.definition.provider;

import lombok.Data;

/**
 * http://www.uxmatters.com/mt/archives/2008/06/international-address-fields-in-web-forms.php
 */
@Data
public class Address {
    private String street;
    private String city;
    private String zip;
    private String state;
    // like Czech Republic, Canada, etc.
    private String country;
}

package lapes.cesupa.ps_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lapes.cesupa.ps_backend.model.Address;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.takeaway-address")
public class TakeawayAddressProperties {
    private String street;
    private String number;
    private String neighbourhood;
    private String city;
    private String state;
    private String zipCode;
    private String complement;

    public Address toAddress() {
        Address address = new Address();
        address.setStreet(this.street);
        address.setNumber(this.number);
        address.setNeighbourhood(this.neighbourhood);
        address.setCity(this.city);
        address.setState(this.state);
        address.setZipCode(this.zipCode);
        address.setComplement(this.complement);
        return address;
    }
}

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EncryptProperty.
 *
 * @author crfischer, Namics AG
 * @since 30.11.2018 09:44
 */
public class EncryptPropertyTest {

    private static Logger LOG = LoggerFactory.getLogger(EncryptPropertyTest.class);

    @Test
    public void encrypt(){

        String password = "myTestSecret";
        String property = "my secret property value";

        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setPassword(password);

        String encryptedValue = stringEncryptor.encrypt(property);

        LOG.info("Property-Value=\"{}\" ==> Encrypted Property-Value=\"{}\"", property, encryptedValue);
    }

    @Test
    public void decrypt(){

        String password = "myTestSecret";
        String encryptedProperty = "nrdDE/a2fVTeWMOceKesa5g1+6yKTat9z6bwwACJVXB/fcA75FaTsA==";

        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setPassword(password);

        String decryptedValue = stringEncryptor.decrypt(encryptedProperty);

        LOG.info("Encrypted Property-Value=\"{}\" ==> Decrypted Property-Value=\"{}\"", encryptedProperty, decryptedValue);
    }
}

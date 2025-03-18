package hexlet.code.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Компонент RsaKeyProperties используется для хранения открытого и закрытого ключей RSA.
 * Класс имеет аннотацию @ConfigurationProperties,
 * которая позволяет Spring загружать значения свойств из файла конфигурации.
 */

@Component
@ConfigurationProperties(prefix = "rsa")
@Getter
@Setter
public class RsaKeyProperties {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
}

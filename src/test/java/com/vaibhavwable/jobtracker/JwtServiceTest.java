package com.vaibhavwable.jobtracker;

import com.vaibhavwable.jobtracker.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-unit-test-secret-0123456789";

    @Test
    void generatedTokenRoundTripsToEmail() {
        JwtService jwt = new JwtService(SECRET, 60_000);
        String token = jwt.generateToken("dev@example.com");
        assertThat(jwt.validateAndExtractEmail(token)).contains("dev@example.com");
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService jwt = new JwtService(SECRET, 60_000);
        String token = jwt.generateToken("dev@example.com");
        assertThat(jwt.validateAndExtractEmail(token + "x")).isEmpty();
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService jwt = new JwtService(SECRET, -1_000);
        String token = jwt.generateToken("dev@example.com");
        assertThat(jwt.validateAndExtractEmail(token)).isEmpty();
    }

    @Test
    void tokenSignedWithDifferentSecretIsRejected() {
        JwtService issuer = new JwtService("another-secret-another-secret-0123456789ab", 60_000);
        JwtService verifier = new JwtService(SECRET, 60_000);
        assertThat(verifier.validateAndExtractEmail(issuer.generateToken("dev@example.com"))).isEmpty();
    }
}

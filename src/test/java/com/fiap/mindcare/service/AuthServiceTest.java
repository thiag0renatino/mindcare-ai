package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.AuthRequestDTO;
import com.fiap.mindcare.dto.AuthSignInDTO;
import com.fiap.mindcare.dto.TokenDTO;
import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fiap.mindcare.security.jwt.JwtTokenProvider;
import com.fiap.mindcare.service.exception.BusinessException;
import com.fiap.mindcare.service.security.PasswordValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UsuarioSistemaRepository usuarioRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidator passwordPolicyValidator;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldRejectInvalidPayload() {
        AuthRequestDTO invalid = new AuthRequestDTO("Nome", "Empresa", "", "");
        assertThrows(BusinessException.class, () -> authService.register(invalid));
    }

    @Test
    void register_shouldRejectExistingEmail() {
        AuthRequestDTO dto = new AuthRequestDTO("Ana", "Acme", "ana@acme.com", "SenhaForte9");
        when(usuarioRepository.existsByEmail("ana@acme.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.register(dto));
    }

    @Test
    void register_shouldCreateUserWhenValid() {
        AuthRequestDTO dto = new AuthRequestDTO("Ana", "Acme", "ana@acme.com", "SenhaForte9");
        Empresa empresa = new Empresa(1L, "12345678901234", "Acme", null);

        when(usuarioRepository.existsByEmail("ana@acme.com")).thenReturn(false);
        when(empresaRepository.findByNomeContainingIgnoreCase("Acme")).thenReturn(Optional.of(empresa));
        when(passwordEncoder.encode("SenhaForte9")).thenReturn("encoded");
        when(usuarioRepository.save(any(UsuarioSistema.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = authService.register(dto);

        ArgumentCaptor<UsuarioSistema> captor = ArgumentCaptor.forClass(UsuarioSistema.class);
        verify(usuarioRepository).save(captor.capture());

        UsuarioSistema saved = captor.getValue();
        assertEquals("Ana", saved.getNome());
        assertEquals("ana@acme.com", saved.getEmail());
        assertEquals("encoded", saved.getSenha());
        assertEquals(TipoUsuario.USER, saved.getTipo());
        assertEquals(empresa, saved.getEmpresa());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void signIn_shouldReturnTokenWhenAuthenticated() {
        AuthSignInDTO dto = new AuthSignInDTO("ana@acme.com", "SenhaForte9");
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setEmail("ana@acme.com");
        usuario.setTipo(TipoUsuario.USER);

        TokenDTO token = new TokenDTO("ana@acme.com", "refresh", "access", new Date(), new Date(), true);

        when(usuarioRepository.findByEmail("ana@acme.com")).thenReturn(Optional.of(usuario));
        when(tokenProvider.createAccessToken(eq("ana@acme.com"), anyList())).thenReturn(token);

        ResponseEntity<TokenDTO> response = authService.signIn(dto);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertNotNull(response.getBody());
        assertEquals("access", response.getBody().getAccessToken());
    }
}

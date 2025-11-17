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
import com.fiap.mindcare.service.security.PasswordValidator;
import com.fiap.mindcare.service.exception.BusinessException;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioSistemaRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordPolicyValidator;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UsuarioSistemaRepository usuarioRepository, EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder, PasswordValidator passwordPolicyValidator) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyValidator = passwordPolicyValidator;
    }

    public ResponseEntity<String> register(AuthRequestDTO credential) {
        if (credential == null || StringUtils.isBlank(credential.getEmail()) || StringUtils.isBlank(credential.getSenha())) {
            throw new BusinessException("Dados de cadastro inválidos");
        }

        if (usuarioRepository.existsByEmail(credential.getEmail())) {
            throw new BusinessException("Já existe um usuário com este e-mail");
        }

        passwordPolicyValidator.validate(credential.getSenha());

        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setNome(credential.getNome());
        usuario.setEmail(credential.getEmail());
        usuario.setSenha(passwordEncoder.encode(credential.getSenha()));
        usuario.setTipo(TipoUsuario.USER);

        Empresa empresa = empresaRepository.findByNomeContainingIgnoreCase(credential.getEmpresa())
                        .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada: " + credential.getEmpresa()));

        usuario.setEmpresa(empresa);

        usuarioRepository.save(usuario);

        return new ResponseEntity<>("Usuário registrado com sucesso!", HttpStatus.CREATED);
    }

    public ResponseEntity<TokenDTO> signIn(AuthSignInDTO credential) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credential.getEmail(),
                        credential.getSenha()
                )
        );

        UsuarioSistema usuario = usuarioRepository.findByEmail(credential.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email: " + credential.getEmail() + " não encontrado"));

        var roles = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        TokenDTO token = tokenProvider.createAccessToken(usuario.getEmail(), roles);
        return ResponseEntity.ok(token);
    }

    public ResponseEntity<TokenDTO> refreshToken(String email, String refreshToken) {
        UsuarioSistema usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário com Email: " + email + " não encontrado"));

        if (StringUtils.isBlank(refreshToken)) {
            throw new BusinessException("Refresh token inválido");
        }

        TokenDTO token = tokenProvider.refreshToken(refreshToken, email);
        return ResponseEntity.ok(token);
    }
}

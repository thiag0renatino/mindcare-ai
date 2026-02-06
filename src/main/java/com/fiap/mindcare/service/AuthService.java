package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.*;
import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fiap.mindcare.security.jwt.JwtTokenProvider;
import com.fiap.mindcare.service.exception.BusinessException;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import com.fiap.mindcare.service.security.PasswordValidator;
import com.fiap.mindcare.service.security.UsuarioAutenticadoProvider;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
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
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UsuarioSistemaRepository usuarioRepository, EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder, PasswordValidator passwordPolicyValidator, UsuarioAutenticadoProvider usuarioAutenticadoProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyValidator = passwordPolicyValidator;
        this.usuarioAutenticadoProvider = usuarioAutenticadoProvider;
    }

    public ResponseEntity<String> register(AuthRequestDTO credential) {
        if (credential == null || StringUtils.isBlank(credential.getEmail()) || StringUtils.isBlank(credential.getSenha())) {
            throw new BusinessException("Dados de cadastro inválidos");
        }

        if (usuarioRepository.existsByEmail(credential.getEmail())) {
            throw new BusinessException("Credenciais inválidas");
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
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));

        var roles = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        TokenDTO token = tokenProvider.createAccessToken(usuario.getEmail(), roles);
        return ResponseEntity.ok(token);
    }

    public ResponseEntity<TokenDTO> refreshToken(String email, String refreshToken) {
        UsuarioSistema usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));

        if (StringUtils.isBlank(refreshToken)) {
            throw new BusinessException("Refresh token inválido");
        }

        TokenDTO token = tokenProvider.refreshToken(refreshToken, email);
        return ResponseEntity.ok(token);
    }

    @Transactional
    public void changePassword(AtualizarSenhaRequestDTO dto) {
        UsuarioSistema usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new BusinessException("Senha atual incorreta");
        }

        if (dto.getSenhaAtual().equals(dto.getSenhaNova())) {
            throw new BusinessException("A nova senha deve ser diferente da senha atual");
        }

        passwordPolicyValidator.validate(dto.getSenhaNova());

        usuario.setSenha(passwordEncoder.encode(dto.getSenhaNova()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void changeName(AtualizarNomeRequestDTO dto) {
        UsuarioSistema usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();

        if (dto.getNomeNovo().equals(usuario.getNome())) {
            throw new BusinessException("O novo nome deve ser diferente do nome atual");
        }

        usuario.setNome(dto.getNomeNovo());
        usuarioRepository.save(usuario);
    }
}

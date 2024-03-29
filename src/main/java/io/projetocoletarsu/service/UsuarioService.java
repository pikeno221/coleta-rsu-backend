package io.projetocoletarsu.service;

import io.projetocoletarsu.exception.ApiException;
import io.projetocoletarsu.model.Usuario;
import io.projetocoletarsu.model.retorno.Retorno;
import io.projetocoletarsu.model.retorno.RetornoTodosUsuarios;
import io.projetocoletarsu.model.retorno.RetornoUsuario;
import io.projetocoletarsu.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private AbstractEmailService emailService;

    public RetornoUsuario criarUsuario(Usuario usuario) throws ApiException {
        RetornoUsuario retorno = new RetornoUsuario();

        try {
            if (usuarioValidoParaInsercao(usuario)) {
                Usuario usuarioCriado = repository.save(usuario);
                retorno.setSucesso(true);
                retorno.setMensagem("Usuario criado com sucesso");
                retorno.setId(usuarioCriado.getId());

            } else {
                retorno.setSucesso(false);
                retorno.setMensagem("Usuario, Email, Telefone ja existente");
            }

            return retorno;

        } catch (Exception e) {
            log.error("Error ao persistir os dados", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro ao persistir dados.");
        }

    }

    public RetornoTodosUsuarios buscarTodosUsuarios() throws ApiException {
        RetornoTodosUsuarios retorno = new RetornoTodosUsuarios();

        try {
            retorno.setUsuarios(repository.findAll());
            retorno.setSucesso(true);

            if (retorno.getUsuarios().isEmpty()) {
                retorno.setMensagem("Nenhum Usuario Encontrado");
            } else {
                retorno.setMensagem("Usuarios buscados com sucesso");
            }

            return retorno;

        } catch (Exception e) {
            log.error("Erro ao buscar os dados", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro ao buscar os dados.");
        }

    }

    public RetornoUsuario buscarUsuarioPorId(Integer idUsuario) throws ApiException {
        RetornoUsuario retorno = new RetornoUsuario();
        Optional<Usuario> usuario;

        try {
            usuario = repository.findById(idUsuario);

        } catch (Exception e) {
            log.error("Erro ao buscar os dados", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro ao buscar os dados.");

        }

        if (usuario.isPresent()) {
            retorno.setUsuario(usuario.get());
            retorno.setMensagem("Sucesso ao buscar usuario");
            retorno.setSucesso(true);
        } else {
            retorno.setUsuario(null);
            retorno.setMensagem("Usuario Nao Encontrado");
            retorno.setSucesso(false);
        }

        return retorno;

    }


    public RetornoUsuario atualizarUsuario(Integer idUsuario, Usuario usuario) throws ApiException {
        try {
            RetornoUsuario retorno;

            retorno = buscarUsuarioPorId(idUsuario);

            if (retorno.isSucesso()) {
                retorno.setUsuario(repository.save(setaValoresAtualizacaoUsuario(retorno.getUsuario(), usuario)));
                retorno.setSucesso(true);
                retorno.setMensagem("Usuario atualizado com sucesso");
            }

            return retorno;

        } catch (Exception e) {
            log.error("Erro ao atualizar os dados do usuário", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro ao buscar os dados.");

        }
    }


    public RetornoUsuario deletarUsuario(Integer idUsuario) throws ApiException {
        RetornoUsuario retorno = new RetornoUsuario();

        Optional<Usuario> usuarioBanco = repository.findById(idUsuario);

        if (usuarioBanco.isPresent()) {

            try {
                repository.delete(usuarioBanco.get());
                retorno.setSucesso(true);
                retorno.setMensagem("Sucesso ao realizar delecao do Usuario. ");

            } catch (Exception e) {
                log.error("Error ao excluir usuario.", e);
                throw new ApiException(422, "Erro ao realizar delecao do usuario");
            }

        } else {
            retorno.setSucesso(false);
            retorno.setMensagem("Usuario nao encontrado");
        }

        return retorno;

    }

    public RetornoUsuario logarUsuario(String email, String senha) throws ApiException {
        Optional<Usuario> usuario = null;

        try {
            usuario = repository.findByEmailAndSenha(email, senha);

            if (usuario.isPresent()) {
                return new RetornoUsuario(true, "Sucesso ao buscar Usuario", usuario.get().getId(), usuario.get());
            } else {
                return new RetornoUsuario(false, "Usuario e/ou Senha invalido", null, null);
            }

        } catch (Exception e) {
            throw new ApiException(422, "Erro ao buscar usuário na base de dados");

        }

    }


    public Retorno recuperarSenhaUsuario(String email) {

        Optional<Usuario> usuario = repository.findByEmail(email);

        if (usuario.isPresent()) {
            emailService.enviarSenhaUsuario(usuario.get());
            return new Retorno(true, "Mensagem enviada para: " + usuario.get().getEmail());
        } else {
            return new Retorno(false, "Usuario nao cadastrado. ");
        }
    }


    private boolean usuarioValidoParaInsercao(Usuario usuario) {
        return true; //!repository.findByNomeCompletoOrCelularOrEmail(usuario.getNomeCompleto(), usuario.getCelular(), usuario.getEmail()).isPresent();
    }

    private Usuario buscarUsuarioPorTelefone(String celular) {
        return repository.findByCelular(celular).get();
    }




    public Usuario buscarUsuarioPorEmail(String email) {
        return repository.findByEmail(email).get();
    }

    private Usuario setaValoresAtualizacaoUsuario(Usuario usuarioBanco, Usuario usuarioDto) {

        if (usuarioDto.getNomeCompleto() != null && !usuarioDto.getNomeCompleto().isEmpty())
            usuarioBanco.setNomeCompleto(usuarioDto.getNomeCompleto());

        if (usuarioDto.getCelular() != null && !usuarioDto.getCelular().isEmpty())
            usuarioBanco.setCelular(usuarioDto.getCelular());


        if (usuarioDto.getEmail() != null && !usuarioDto.getEmail().isEmpty())
            usuarioBanco.setEmail(usuarioDto.getEmail());


        if (usuarioDto.getEndereco() != null && !usuarioDto.getEndereco().isEmpty())
            usuarioBanco.setEndereco(usuarioDto.getEndereco());

        if (usuarioDto.getSenha() != null && !usuarioDto.getSenha().isEmpty())
            usuarioBanco.setSenha(usuarioDto.getSenha());

        if (usuarioDto.getStatusUsuario() != null)
            usuarioBanco.setStatusUsuario(usuarioDto.getStatusUsuario());

        if (usuarioDto.getSenha() != null && !usuarioDto.getSenha().isEmpty())
            usuarioBanco.setSenha(usuarioDto.getSenha());

        if (usuarioDto.getTipoPessoa() != null) {
            usuarioBanco.setTipoPessoa(usuarioDto.getTipoPessoa());
        }

        return usuarioBanco;

    }

}

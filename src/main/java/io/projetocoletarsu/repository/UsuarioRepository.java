package io.projetocoletarsu.repository;

import io.projetocoletarsu.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCelular(String celular);

    Optional<Usuario> findByNomeCompletoOrCelularOrEmail(String nomeCompleto, String celular, String email);

    Optional<Usuario> findByEmailAndSenha(Optional<Usuario> usuario, String senha);
}

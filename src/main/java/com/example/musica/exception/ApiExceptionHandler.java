package com.example.musica.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(RecursoNaoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail rotaNaoEncontrada(NoResourceFoundException ex) {
        return problema(HttpStatus.NOT_FOUND, "Rota não encontrada", "A rota " + ex.getResourcePath() + " não existe");
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail conflito(ConflitoException ex) {
        return problema(HttpStatus.CONFLICT, "Conflito", ex.getMessage());
    }

    // rede de segurança: os services já checam duplicidade, mas duas requisições ao mesmo tempo podem passar
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridade(DataIntegrityViolationException ex) {
        return problema(HttpStatus.CONFLICT, "Conflito", "A operação viola uma restrição do banco de dados");
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ProblemDetail regraNegocio(RegraNegocioException ex) {
        return problema(HttpStatus.UNPROCESSABLE_CONTENT, "Regra de negócio violada", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage()));

        ProblemDetail problem = problema(HttpStatus.BAD_REQUEST, "Erro de validação", "Um ou mais campos são inválidos");
        problem.setProperty("errors", erros);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail jsonInvalido(HttpMessageNotReadableException ex) {
        return problema(HttpStatus.BAD_REQUEST, "JSON inválido", "O corpo da requisição está mal formatado ou tem tipos errados");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Parâmetro inválido",
                "O valor '" + ex.getValue() + "' não é válido para o parâmetro " + ex.getName());
    }

    // sem este handler, um @RequestParam obrigatório ausente cairia no erro interno (500)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail parametroAusente(MissingServletRequestParameterException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório", "O parâmetro " + ex.getParameterName() + " é obrigatório");
    }

    // acontece quando o cliente manda ?sort= com um campo que não existe na entidade
    @ExceptionHandler({ PropertyReferenceException.class, InvalidDataAccessApiUsageException.class })
    public ProblemDetail ordenacaoInvalida(RuntimeException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Ordenação inválida", "O campo informado em sort não existe");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail erroInterno(Exception ex) {
        // o detalhe vai só pro log, o cliente não vê stacktrace
        log.error("Erro inesperado", ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Ocorreu um erro inesperado no servidor");
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalhe) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(titulo);
        problem.setDetail(detalhe);
        return problem;
    }
}
package net.purocodigo.encuestabackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;

import net.purocodigo.encuestabackend.entities.PollEntity;
import net.purocodigo.encuestabackend.entities.UserEntity;
import net.purocodigo.encuestabackend.models.requests.PollCreationRequestModel;
import net.purocodigo.encuestabackend.models.requests.UserLoginRequestModel;
import net.purocodigo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.purocodigo.encuestabackend.models.responses.PaginatedPollRest;
import net.purocodigo.encuestabackend.models.responses.PollRest;
import net.purocodigo.encuestabackend.models.responses.PollResultWrapperRest;
import net.purocodigo.encuestabackend.models.responses.ValidationErrors;
import net.purocodigo.encuestabackend.repositories.PollRepository;
import net.purocodigo.encuestabackend.repositories.UserRepository;
import net.purocodigo.encuestabackend.services.PollService;
import net.purocodigo.encuestabackend.services.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class PollControllerTests {

    private static final String API_URL = "/polls";

    private static final String API_LOGIN_URL = "/users/login";

    private String token = "";

    private UserEntity user = null;
        
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserService userService;

    @Autowired
    PollService pollService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PollRepository pollRepository;

    @BeforeAll
    public void initializeObjects() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        UserRegisterRequestModel user = TestUtil.createValidUser();
        this.user = userService.createUser(user);
        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());
        ResponseEntity<Map<String, String>> response = 
            login(model, new ParameterizedTypeReference<Map<String, String>>(){});

        Map<String, String> body = response.getBody();
        this.token = body.get("token");
    }

    @AfterEach
    public void cleanup() {
        pollRepository.deleteAll();
    }

    @AfterAll
    public void cleanupAfter() {
        userRepository.deleteAll();
    }

    // #region createPoll

    @Test
    public void createPoll_sinAutenticacion_retornaForbidden() {
        ResponseEntity<Object> response = createPoll(new PollCreationRequestModel(), Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void createPoll_conAutenticacionSinDatos_retornaBadRequest() {
        ResponseEntity<ValidationErrors> response = createPoll(
            new PollCreationRequestModel(), new ParameterizedTypeReference<ValidationErrors>(){}
        );
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_conAutenticacionSinElContenidoDeLaEncuesta_retornaBadRequest() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_conAutenticacionSinElContenidoDeLaEncuesta_retornaValidationErrorParaElContenido() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("content"));
    }

    @Test
    public void createPoll_conAutenticacionSinPreguntasParaLaEncuesta_retornaBadRequest() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.setQuestions(null);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_conAutenticacionSinPreguntasParaLaEncuesta_retornaValidationErrorParaPreguntas() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.setQuestions(null);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions"));
    }   

    @Test
    public void createPoll_conAutenticacionConPreguntaValidaSinContenido_retornaBadRequest() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_conAutenticacionConPreguntaValidaSinContenido_retornaValidationErrorParaContenidoDePregunta() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].content"));
    }

    @Test
    public void createPoll_conAutenticacionConPreguntaValidaConOrdenIncorrecto_retornaBadRequest() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setQuestionOrder(0);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_conAutenticacionConPreguntaValidaConOrdenIncorrecto_retornaValidationErrorParaOrdenDePregunta() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setQuestionOrder(0);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].questionOrder"));
    }

    @Test
    public void createPoll_conAutenticacionConPreguntaValidaConQuestionTypeIncorrecto_retornaBadRequest() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setType("sasda");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_conAutenticacionConPreguntaValidaConQuestionTypeIncorrecto_retornaValidationErrorParaQuestionType() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setType("sasda");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].type"));
    }

    @Test
    public void createPoll_conAutenticacionConPreguntaValidaSinRepuestas_retornaBadRequest() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setAnswers(null);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_conAutenticacionConPreguntaValidaSinRepuestas_retornaValidationErrorParaListaDeRespuestas() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).setAnswers(null);
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].answers"));
    }

    @Test
    public void createPoll_conAutenticacionSinContenidoEnLaRespuesta_retornaBadRequest() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).getAnswers().get(0).setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createPoll_conAutenticacionSinContenidoEnLaRespuesta_retornaValidationErrorParaContenidoDeRespuesta() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        poll.getQuestions().get(0).getAnswers().get(0).setContent("");
        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});
        assertTrue(response.getBody().getErrors().containsKey("questions[0].answers[0].content"));
    }

    @Test
    public void createPoll_conAutenticacionConEncuestaValida_retornaOK() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        ResponseEntity<Object> response = createPoll(poll, new ParameterizedTypeReference<Object>(){});
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void createPoll_conAutenticacionConEncuestaValida_retornaCreatedPollId() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        ResponseEntity<Map<String,String>> response = createPoll(
            poll, new ParameterizedTypeReference<Map<String,String>>(){}
        );
        Map<String,String> body = response.getBody();
        assertTrue(body.containsKey("pollId"));
    }

    @Test
    public void createPoll_conAutenticacionConEncuestaValida_guardaEnBaseDeDatos() {
        PollCreationRequestModel poll = TestUtil.createValidPoll();
        ResponseEntity<Map<String,String>> response = createPoll(
            poll, new ParameterizedTypeReference<Map<String,String>>(){}
        );
        Map<String,String> body = response.getBody();
        PollEntity pollDB = pollRepository.findByPollId(body.get("pollId"));
        assertNotNull(pollDB);
    }

    //#endregion

    // #region getPollWithQuestions
    @Test
    public void getPollWithQuestions_cuandoLaEncuestaNoExisteEnLaBaseDeDatos_retornaInternalServerError() {
        ResponseEntity<Object> response = getPollWithQuestions(API_URL + "/uuid/questions", Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getPollWithQuestions_cuandoLaEncuestaExiste_retornaOk() {
        PollCreationRequestModel model = TestUtil.createValidPoll();
        String uuid = pollService.createPoll(model, user.getEmail());
        ResponseEntity<Object> response = getPollWithQuestions(API_URL + "/" + uuid + "/questions", Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getPollWithQuestions_cuandoLaEncuestaExiste_retornaPollRest() {
        PollCreationRequestModel model = TestUtil.createValidPoll();
        String uuid = pollService.createPoll(model, user.getEmail());
        ResponseEntity<PollRest> response = getPollWithQuestions(API_URL + "/" + uuid + "/questions", PollRest.class);
        assertEquals(uuid, response.getBody().getPollId());
    }

    //#endregion

    // #region getPolls

    @Test
    public void getPolls_sinAutenticacion_retornaForbidden() {        
        ResponseEntity<Object> response = getPolls(API_URL, false, new ParameterizedTypeReference<Object>(){});
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void getPolls_conAutenticacion_retornaOK() {        
        ResponseEntity<Object> response = getPolls(API_URL, true, new ParameterizedTypeReference<Object>(){});
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getPolls_conAutenticacion_retornaPaginatedPollRest() {         
        pollRepository.save(TestUtil.createValidPollEntity(user));    
        ResponseEntity<PaginatedPollRest> response = getPolls(API_URL, true, new ParameterizedTypeReference<PaginatedPollRest>(){});
        List<PollRest> polls = response.getBody().getPolls();     
        assertEquals(polls.size(), 1);
    }

    @Test
    public void getPolls_conAutenticacion_retornaDatosDePaginacion() {         
        pollRepository.save(TestUtil.createValidPollEntity(user));    
        ResponseEntity<PaginatedPollRest> response = getPolls(API_URL, true, new ParameterizedTypeReference<PaginatedPollRest>(){});
        assertEquals(response.getBody().getCurrentPage(), 1);
        assertEquals(response.getBody().getCurrentPageRecords(), 1);
        assertEquals(response.getBody().getTotalPages(), 1);
        assertEquals(response.getBody().getTotalRecords(), 1);
    }

    @Test
    public void getPolls_conAutenticacionConParametroLimit_retornaPollsLimitadasPorLimit() {         
        pollRepository.save(TestUtil.createValidPollEntity(user));    
        pollRepository.save(TestUtil.createValidPollEntity(user));    
        pollRepository.save(TestUtil.createValidPollEntity(user));    
        ResponseEntity<PaginatedPollRest> response = getPolls(
            true,
            new ParameterizedTypeReference<PaginatedPollRest>(){},
            2
        );
        List<PollRest> polls = response.getBody().getPolls();
        assertEquals(polls.size(), 2);
        
    }

    @Test
    public void getPolls_conAutenticacionConParametroPage_retornaPollsDeLaSegundaPagina() {         
        pollRepository.save(TestUtil.createValidPollEntity(user));    
        pollRepository.save(TestUtil.createValidPollEntity(user));    
        pollRepository.save(TestUtil.createValidPollEntity(user));    
        ResponseEntity<PaginatedPollRest> response = getPolls(
            true,
            new ParameterizedTypeReference<PaginatedPollRest>(){},
            2, 1
        );
        List<PollRest> polls = response.getBody().getPolls();
        assertEquals(polls.size(), 1);
        
    }
    //#endregion

    // #region togglePollOpened

    @Test
    public void togglePollOpened_sinAutenticacion_retornaForbidden() {         
        ResponseEntity<Object> response = togglePollOpened(false, "abc", new ParameterizedTypeReference<Object>(){});
        
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }


    @Test
    public void togglePollOpened_conAutenticacionAUnaEncuestaQueNoPerteneceAlUsuario_retornaInternalServerError() {         
        UserEntity otherUser = userService.createUser(TestUtil.createValidUser());
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(otherUser)); 
        ResponseEntity<Object> response = togglePollOpened(true, poll.getPollId(), new ParameterizedTypeReference<Object>(){});
         assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void togglePollOpened_conAutenticacionAUnaEncuestaExistenteDelUsuario_retornaOk() {
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user)); 
        ResponseEntity<Object> response = togglePollOpened(true, poll.getPollId(), new ParameterizedTypeReference<Object>(){});
         assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void togglePollOpened_conAutenticacionAUnaEncuestaExistenteDelUsuario_cambiaOpenedEnLaBaseDeDatos() {
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user)); 
        togglePollOpened(true, poll.getPollId(), new ParameterizedTypeReference<Object>(){});
        PollEntity updatedPoll = pollRepository.findById(poll.getId());
        assertEquals(updatedPoll.isOpened(), false);
    }

    //#endregion

    // #region deletePoll

    @Test
    public void deletePoll_sinAutenticacion_retornaForbidden() {         
        ResponseEntity<Object> response = deletePoll(false, "abc", new ParameterizedTypeReference<Object>(){});
        
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }


    @Test
    public void deletePoll_conAutenticacionAUnaEncuestaQueNoPerteneceAlUsuario_retornaInternalServerError() {         
        UserEntity otherUser = userService.createUser(TestUtil.createValidUser());
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(otherUser)); 
        ResponseEntity<Object> response = deletePoll(true, poll.getPollId(), new ParameterizedTypeReference<Object>(){});
         assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void deletePoll_conAutenticacionAUnaEncuestaExistenteDelUsuario_retornaOk() {
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user)); 
        ResponseEntity<Object> response = deletePoll(true, poll.getPollId(), new ParameterizedTypeReference<Object>(){});
         assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void deletePoll_conAutenticacionAUnaEncuestaExistenteDelUsuario_EliminaPollEnLaBaseDeDatos() {
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user)); 
        deletePoll(true, poll.getPollId(), new ParameterizedTypeReference<Object>(){});
        PollEntity deletedPoll = pollRepository.findById(poll.getId());
        assertNull(deletedPoll);
    }

    //#endregion

    // #region getResults

    @Test
    public void getResults_sinAutenticacion_retornaForbidden() {         
        ResponseEntity<Object> response = getResults(false, "abc", new ParameterizedTypeReference<Object>(){});   
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }


    @Test
    public void getResults_conAutenticacionAUnaEncuestaQueNoExiste_retornaInternalServerError() {         
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user)); 
        ResponseEntity<Object> response = getResults(true, poll.getPollId() + "abc", new ParameterizedTypeReference<Object>(){});
         assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getResults_conAutenticacionAUnaEncuestaQueNoPeteneceAlUsuario_retornaInternalServerError() {         
        UserEntity otherUser = userService.createUser(TestUtil.createValidUser());
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(otherUser)); 
        ResponseEntity<Object> response = getResults(true, poll.getPollId(), new ParameterizedTypeReference<Object>(){});
         assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getResults_conAutenticacionAUnaEncuestaExistenteDelUsuario_retornaOK() {         
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user)); 
        ResponseEntity<Object> response = getResults(true, poll.getPollId(), new ParameterizedTypeReference<Object>(){});
         assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getResults_conAutenticacionAUnaEncuestaExistenteDelUsuario_retornaPollResultWrapperRest() {         
        PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user)); 
        ResponseEntity<PollResultWrapperRest> response = getResults(true, poll.getPollId(), new ParameterizedTypeReference<PollResultWrapperRest>(){});
        assertEquals(poll.getContent(), response.getBody().getContent());
    }

    //#endregion


    public <T> ResponseEntity<T> getPollWithQuestions(String url, Class<T> responseType) {
        return testRestTemplate.getForEntity(url, responseType);
    }

    public <T> ResponseEntity<T> createPoll(PollCreationRequestModel data, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_URL, data, responseType);
    }

    public <T> ResponseEntity<T> createPoll(PollCreationRequestModel data, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<PollCreationRequestModel> entity = new HttpEntity<PollCreationRequestModel>(data, headers);
        return testRestTemplate.exchange(API_URL, HttpMethod.POST, entity, responseType);
    }

    public <T> ResponseEntity<T> getPolls(boolean auth, ParameterizedTypeReference<T> responseType, int limit) {
        String url = API_URL + "?limit=" + limit;
        return getPolls(url, auth, responseType);
    }   

    public <T> ResponseEntity<T> getPolls(boolean auth, ParameterizedTypeReference<T> responseType, int limit, int page) {
        String url = API_URL + "?limit=" + limit + "&page=" + page;
        return getPolls(url, auth, responseType);
    }   

    public <T> ResponseEntity<T> togglePollOpened(boolean auth, String pollId, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (auth) {
            headers.setBearerAuth(token);
        }
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        String url = API_URL + "/" + pollId;
        return testRestTemplate.exchange(url, HttpMethod.PATCH, entity, responseType);
    }

    public <T> ResponseEntity<T> deletePoll(boolean auth, String pollId, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (auth) {
            headers.setBearerAuth(token);
        }
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        String url = API_URL + "/" + pollId;
        return testRestTemplate.exchange(url, HttpMethod.DELETE, entity, responseType);
    }
    
    public <T> ResponseEntity<T> getPolls(String url, boolean auth, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (auth) {
            headers.setBearerAuth(token);
        }
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }

    public <T> ResponseEntity<T> getResults(boolean auth, String pollId, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (auth) {
            headers.setBearerAuth(token);
        }
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        String url = API_URL + "/" + pollId + "/results";
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }
    


    public <T> ResponseEntity<T> login(UserLoginRequestModel data, ParameterizedTypeReference<T> responseType) {
        HttpEntity<UserLoginRequestModel> entity = new HttpEntity<UserLoginRequestModel>(data, new HttpHeaders());
        return testRestTemplate.exchange(API_LOGIN_URL, HttpMethod.POST, entity, responseType);
    }
}

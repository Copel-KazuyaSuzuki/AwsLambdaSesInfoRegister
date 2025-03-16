package copel.sesproductpackage.register;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

import copel.sesproductpackage.register.api.RequestObject;

class LambdaHandlerTest {
    private LambdaHandler lambdaHandler;
    
    @Mock
    private Context context;
    
    @Mock
    private RequestObject requestObject;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lambdaHandler = new LambdaHandler();
        LambdaLogger mockLogger = mock(LambdaLogger.class);
        when(context.getLogger()).thenReturn(mockLogger);
    }

    @Test
    void testHandleRequest_withValidSkillSheetRequest() throws IOException, InterruptedException {
        SQSEvent event = createSQSEvent("{\"request_type\":\"12\"}");
        when(requestObject.isValid()).thenReturn(true);
        when(requestObject.isスキルシート()).thenReturn(true);
        
        String result = lambdaHandler.handleRequest(event, context);
        
        assertEquals("process complete.", result);
    }

    @Test
    void testHandleRequest_withInvalidRequest() {
        SQSEvent event = createSQSEvent("{\"request_type\":\"不正\"}");
        
        String result = lambdaHandler.handleRequest(event, context);
        
        assertEquals("process complete.", result);
    }

    @Test
    void testHandleRequest_withEmptySQSEvent() {
        SQSEvent event = new SQSEvent();
        event.setRecords(Collections.emptyList());
        
        String result = lambdaHandler.handleRequest(event, context);
        
        assertEquals("process failed.", result);
    }

    private SQSEvent createSQSEvent(String messageBody) {
        SQSEvent event = new SQSEvent();
        SQSMessage message = new SQSMessage();
        message.setBody(messageBody);
        event.setRecords(Collections.singletonList(message));
        return event;
    }
}

package org.zalando.logbook.spring;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.MockRawHttpRequest;
import org.zalando.logbook.Precorrelation;

import java.io.IOException;
import java.util.function.Function;

import static org.hamcrest.Matchers.containsString;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@LogbookTest(properties = "logbook.format.style = curl")
class FormatStyleCurlTest {

    @Autowired
    private Logbook logbook;

    @MockBean
    private HttpLogWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        doReturn(true).when(writer).isActive(any());
    }

    @Test
    void shouldUseHttpFormatter() throws IOException {
        logbook.write(MockRawHttpRequest.create());

        verify(writer).writeRequest(argThat(isCurlFormatter()));
    }

    private Matcher<Precorrelation<String>> isCurlFormatter() {
        final Function<Precorrelation<String>, String> getRequest = Precorrelation::getRequest;
        return hasFeature("request", getRequest, containsString("curl -v -X GET 'http://localhost/'"));
    }

}

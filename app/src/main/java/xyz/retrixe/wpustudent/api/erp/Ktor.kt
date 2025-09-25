package xyz.retrixe.wpustudent.api.erp

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.cookie
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val BASE_URL = "https://erp.mitwpu.edu.in/"

val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

fun createHttpClient(token: String?): HttpClient = HttpClient(
    if (token == "TestAccount|TestAccount") MockEngineFactory else Android
) {
    println(token)
    expectSuccess = true
    BrowserUserAgent()
    install(Logging) {
        level = LogLevel.INFO
    }
    install(ContentNegotiation) {
        json(json)
    }
    defaultRequest {
        url(BASE_URL)
        if (token != null) {
            cookie("AuthToken", token.split("|")[0])
            cookie("ASP.NET_SessionId", token.split("|")[1])
        }
    }
}

const val USER_IMG = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAACgUlEQVR4AeyWO2gVQRSGg9jZqKgogtgJFj4aEXyAjQqKiIW1L7RRsPFRCAoWolgqCuKrFlEUBW0UxMLCLqRIlSIkBEJIkzbk+8LMQjY7O7N5Qkg4X85k5v//PWzuvXvX9Czzz+oAc7kDZ/mv3YOPMBhw7Z5nbJVX1wFeEv0JHsI52B5w7Z5natguq9IBthH3A67BKNyCE7A14No9z9So1cNxe5UMsI6Ib3Ac/sAueAo/YSTg2j3P1KjVoxdJukoGuIt9P7yDozAGqfJMjVo9elPa6f3cAAdRGTJAvw6lpVaPXjOSvtwABqzF/QImoLTU6tFrRtKXG2BHcPaF3qVFT8xo9OYGWB9c/0Pv0qInZjR6cwNsCK7J0Lu06IkZjd7cAHH6vY3u9s3oiRmN6twAT4JrT+hdWvTEjEZvboD3wXWHvhFKS60e9THD9SxyA/hK/oBrE3yG0lKrR68ZSV9uAI0+5XxFH+GPt9D28eqZGrV69GJJV8kA/dhPwm+4AL1wG06DDxxx7Z5natTq0YssXSUD6PYpd4zFF9gJj+ErDAVcu+eZGrV6OG6vkgF8G/lx+o+oM5ArNWr16G3V5wbwG84vEh7BARiG53AZDsPmgGv3PFOjVo9eM5A1V9sAD7D4DWcf/Ttcgd3gk+4N/S94m8W1e56pUatHrxlmIZ9dqQE03A/yV/RT8BrGIVdq1OrRq94sM13PoGmAmyg00Hou8esqzLX0mqHfTLNdV9QH8JYpVHCRX76nafMqM8wyxGyv4Xqa+gAKfOU+49SvVbQFKbPMNNtrVKH1AbZw4sfnDfpCl5lme40quz7AIU7Ow2KV2V6jyq8PUB0s1WLlD5C7k1MAAAD//8L+yFcAAAAGSURBVAMA5mFyQRCxvu8AAAAASUVORK5CYII="

data object MockEngineFactory : HttpClientEngineFactory<MockEngineConfig> {
    override fun create(block: MockEngineConfig.() -> Unit): HttpClientEngine =
        MockEngine.create {
            block(this)
            requestHandlers.add { request ->
                when (request.url.encodedPath) {
                    "/ERP_Main.aspx" -> {
                        respond(
                            // Fully synthetic content
                            """
                                    <html>
                                    <body>
                                    <span id="span_userid">1032233137</span>
                                    <h6 id="span_username">John Doe</h6>
                                    <span id="span_regular">SEMESTER-V(First)</span>
                                    <span id="span_courseyear">TY BTech  -CSE-A</span>
                                    <img id="imgprofile" src="$USER_IMG" />
                                    </body>
                                    </html>
                                """.trimIndent(),
                            HttpStatusCode.OK
                        )
                    }
                    "/STUDENT/SelfAttendence.aspx" -> {
                        respond(
                            // Taken from my attendance, removed `style` and `onclick` attributes
                            """
                                    <html>
                                    <body>
                                    <div class="infor-table">
                                    <div id="divMain" class="table table-responsive">
                                      <div>
                                        <table class="table table-bordered table-condensed table-striped ">
                                          <thead>
                                            <tr>
                                              <th>SrNo</th>
                                              <th>Subject</th>
                                              <th>Subject Type</th>
                                              <th>Present</th>
                                              <th>Total Period</th>
                                              <th>Percentage (%)</th>
                                            </tr>
                                          </thead>
                                          <tbody>
                                            <tr class="tblAltRowStyle">
                                              <td rowspan="2">1</td>
                                              <td title="Bigdata Technologies" rowspan="2">Bigdata Technologies </td>
                                              <td title="TH"><a href="#" id="1#87844" runat="server"><span class="countn bluedrk" data-toggle="modal">TH</span></a></td>
                                              <td> 27</td>
                                              <td> 31</td>
                                              <td> 87.10</td>
                                            </tr>
                                            <tr class="tblAltRowStyle">
                                              <td title="PR"><a href="#" id="2#87844" runat="server"><span class="countn bluedrk" data-toggle="modal">PR</span></a></td>
                                              <td> 16</td>
                                              <td> 18</td>
                                              <td> 88.89</td>
                                            </tr>
                                            <tr class="tblRowStyle">
                                              <td rowspan="1">2</td>
                                              <td title="Operating System" rowspan="1">Operating System </td>
                                              <td title="TH"><a href="#" id="1#87847" runat="server"><span class="countn bluedrk" data-toggle="modal">TH</span></a></td>
                                              <td> 24</td>
                                              <td> 32</td>
                                              <td> 75</td>
                                            </tr>
                                            <tr class="tblAltRowStyle">
                                              <td rowspan="1">3</td>
                                              <td title="Operating System Laboratory" rowspan="1">Operating System Laboratory </td>
                                              <td title="PR"><a href="#" id="2#87848" runat="server"><span class="countn bluedrk" data-toggle="modal">PR</span></a></td>
                                              <td> 16</td>
                                              <td> 16</td>
                                              <td> 100</td>
                                            </tr>
                                            <tr class="tblRowStyle">
                                              <td rowspan="1">4</td>
                                              <td title="Internet of Things Laboratory" rowspan="1">Internet of Things Laboratory </td>
                                              <td title="PR"><a href="#" id="2#87849" runat="server"><span class="countn bluedrk" data-toggle="modal">PR</span></a></td>
                                              <td> 18</td>
                                              <td> 22</td>
                                              <td> 81.82</td>
                                            </tr>
                                            <tr class="tblAltRowStyle">
                                              <td rowspan="1">5</td>
                                              <td title="Artificial Intelligence and Expert Systems" rowspan="1">Artificial Intelligence and Expert Systems </td>
                                              <td title="TH"><a href="#" id="1#87850" runat="server"><span class="countn bluedrk" data-toggle="modal">TH</span></a></td>
                                              <td> 30</td>
                                              <td> 32</td>
                                              <td> 93.75</td>
                                            </tr>
                                            <tr class="tblRowStyle">
                                              <td rowspan="1">6</td>
                                              <td title="Artificial Intelligence and Expert Systems Laboratory" rowspan="1">Artificial Intelligence and Expert Systems Laboratory </td>
                                              <td title="PR"><a href="#" id="2#87851" runat="server"><span class="countn bluedrk" data-toggle="modal">PR</span></a></td>
                                              <td> 24</td>
                                              <td> 24</td>
                                              <td> 100</td>
                                            </tr>
                                            <tr class="tblAltRowStyle">
                                              <td rowspan="1">7</td>
                                              <td title="Software Engineering and Modelling" rowspan="1">Software Engineering and Modelling </td>
                                              <td title="TH"><a href="#" id="1#87852" runat="server"><span class="countn bluedrk" data-toggle="modal">TH</span></a></td>
                                              <td> 32</td>
                                              <td> 35</td>
                                              <td> 91.43</td>
                                            </tr>
                                            <tr class="tblRowStyle">
                                              <td rowspan="1">8</td>
                                              <td title="Project Based Learning - III" rowspan="1">Project Based Learning - III </td>
                                              <td title="PJ"><a href="#" id="5#87853" runat="server"><span class="countn bluedrk" data-toggle="modal">PJ</span></a></td>
                                              <td> 10</td>
                                              <td> 10</td>
                                              <td> 100</td>
                                            </tr>
                                            <tr class="tblAltRowStyle">
                                              <td rowspan="1">9</td>
                                              <td title="Managing Conflicts Peacefully: Tools and Techniques" rowspan="1">Managing Conflicts Peacefully: Tools and Techniques </td>
                                              <td title="TH"><a href="#" id="1#87854" runat="server"><span class="countn bluedrk" data-toggle="modal">TH</span></a></td>
                                              <td> 18</td>
                                              <td> 18</td>
                                              <td> 100</td>
                                            </tr>
                                            <tr class="danger">
                                              <td colspan="2"></td>
                                              <td><b>Theory</b></td>
                                              <td>131</td>
                                              <td>148</td>
                                              <td>88.51</td>
                                            </tr>
                                            <tr class="success">
                                              <td colspan="2"></td>
                                              <td><b>Practical</b></td>
                                              <td>74</td>
                                              <td>80</td>
                                              <td>92.5</td>
                                            </tr>
                                            <tr class="warning">
                                              <td colspan="2"></td>
                                              <td><b>Tutorial</b></td>
                                              <td>0</td>
                                              <td>0</td>
                                              <td>0</td>
                                            </tr>
                                            <tr class="info">
                                              <td colspan="2"></td>
                                              <td><b>Total</b></td>
                                              <td>215</td>
                                              <td>238</td>
                                              <td>90.34</td>
                                            </tr>
                                          </tbody>
                                        </table>
                                      </div>
                                    </div>
                                    </div>
                                    </body>
                                    </html>
                                """.trimIndent(),
                            HttpStatusCode.OK
                        )
                    }
                    else -> respond("Unknown API called", HttpStatusCode.NotFound)
                }
            }
        }
}

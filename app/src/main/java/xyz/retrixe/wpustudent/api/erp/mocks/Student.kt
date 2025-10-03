package xyz.retrixe.wpustudent.api.erp.mocks

import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respondOk
import xyz.retrixe.wpustudent.api.erp.USER_IMG

val mockRetrieveStudentBasicInfo: MockRequestHandler = {
    respondOk(
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
        """.trimIndent()
    )
}

val mockGetAttendanceSummary: MockRequestHandler = {
    respondOk(
        // Taken from my attendance, removed `style` and `onclick` attributes
        """
            <html>
            <body>
            <script>
            // This is completely synthetic
            var varSemId = '20';
            </script>
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
        """.trimIndent()
    )
}

val mockGetAttendanceDetails: MockRequestHandler = {
    // FIXME
    respondOk("")
}

package server.api;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import server.database.repository.EventStatRepository;

@Controller
public class GradeController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EventStatRepository eventStatRepository;

	/* Default % gaps */
	private static final int gaps[][] = { { 0, 60 }, { 61, 90 }, { 91, 100 } };

	@GetMapping(path = "/api/grade")
	public ResponseEntity<String> grade() {
		try {
			DateTime day = new DateTime().withMillisOfDay(0);
			Long nEventStat = eventStatRepository.countByDate(day.toDate());
			while (nEventStat == 0) {
				day = day.minusDays(1);
				nEventStat = eventStatRepository.countByDate(day.toDate());
			}

			String grades = "";
			for (int i = 0; i < gaps.length; i++) {
				Long count = eventStatRepository.countByDateAndGradeBetween(day.toDate(), gaps[i][0], gaps[i][1]);
				Integer grade = (int) (count * 100 / nEventStat);
				grades += grade.toString() + ", ";
			}
			grades += nEventStat.toString();

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
			return new ResponseEntity<>(grades, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

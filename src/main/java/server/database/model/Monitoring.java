/*package server.database.model;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Monitoring {
	private Date date;
	// /usr/bin/free -tmo | grep -i Mem: | awk '{print $2}'
	private Integer memTotal;
	// /usr/bin/free -tmo | grep -i Mem: | awk '{print $4+$6+$7}'
	private Integer memFree;
	// df -hl | grep /dev/root | awk '{ print $2}'
	private Integer hddSize;
	// df -hl | grep /dev/root | awk '{ print $3}'
	private Integer hddUsed;
	// uptime -s
	private Long lastBoot;
	// uptime -p
	private String uptime;
	// systemctl status z-way-server.service | grep running | wc -l
	private Boolean running;
}*/

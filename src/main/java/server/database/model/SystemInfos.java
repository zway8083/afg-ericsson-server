package server.database.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class SystemInfos {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long id;

	@ManyToOne(optional = false)
	@JsonIgnore
	private Raspberry raspberry;

	private Date date;
	private Integer memTotal;
	private Integer memFree;
	private Integer hddSize;
	private Integer hddUsed;
	private Date lastBoot;
	private String uptime;
	private Boolean running;

	public SystemInfos() {
	}

	public Raspberry getRaspberry() {
		return raspberry;
	}

	public void setRaspberry(Raspberry raspberry) {
		this.raspberry = raspberry;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = new Date(date * 1000);
	}

	public Integer getMemTotal() {
		return memTotal;
	}

	public void setMemTotal(Integer memTotal) {
		this.memTotal = memTotal;
	}

	public Integer getMemFree() {
		return memFree;
	}

	public void setMemFree(Integer memFree) {
		this.memFree = memFree;
	}

	public Integer getHddSize() {
		return hddSize;
	}

	public void setHddSize(Integer hddSize) {
		this.hddSize = hddSize;
	}

	public Integer getHddUsed() {
		return hddUsed;
	}

	public void setHddUsed(Integer hddUsed) {
		this.hddUsed = hddUsed;
	}

	public Date getLastBoot() {
		return lastBoot;
	}

	public void setLastBoot(Long lastBoot) {
		this.lastBoot = new Date(lastBoot * 1000);
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public Boolean getRunning() {
		return running;
	}

	public void setRunning(Boolean running) {
		this.running = running;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SystemInfos [" + (id != null ? "id=" + id + ", " : "")
				+ (raspberry != null ? "raspberry=" + raspberry.getId() + ", " : "")
				+ (date != null ? "date=" + date + ", " : "") + (memTotal != null ? "memTotal=" + memTotal + ", " : "")
				+ (memFree != null ? "memFree=" + memFree + ", " : "")
				+ (hddSize != null ? "hddSize=" + hddSize + ", " : "")
				+ (hddUsed != null ? "hddUsed=" + hddUsed + ", " : "")
				+ (lastBoot != null ? "lastBoot=" + lastBoot + ", " : "")
				+ (uptime != null ? "uptime=" + uptime + ", " : "") + (running != null ? "running=" + running : "")
				+ "]";
	}

}
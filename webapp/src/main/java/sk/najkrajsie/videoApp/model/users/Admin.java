package sk.najkrajsie.videoApp.model.users;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "ADMIN")
@DiscriminatorValue("ADMIN")
public class Admin extends User {

	public Admin() {
	}
}

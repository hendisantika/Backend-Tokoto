package com.apiecommerce.tokoto;

import com.apiecommerce.tokoto.auth.AuthenticationService;
import com.apiecommerce.tokoto.auth.RegisterRequest;
import com.apiecommerce.tokoto.gender.Gender;
import com.apiecommerce.tokoto.gender.GenderService;
import com.apiecommerce.tokoto.location.district.District;
import com.apiecommerce.tokoto.location.district.DistrictRepository;
import com.apiecommerce.tokoto.location.district.DistrictService;
import com.apiecommerce.tokoto.location.province.Province;
import com.apiecommerce.tokoto.location.province.ProvinceRepository;
import com.apiecommerce.tokoto.location.province.ProvinceService;
import com.apiecommerce.tokoto.location.regency.Regency;
import com.apiecommerce.tokoto.location.regency.RegencyRepository;
import com.apiecommerce.tokoto.location.regency.RegencyResponse;
import com.apiecommerce.tokoto.location.regency.RegencyService;
import com.apiecommerce.tokoto.location.village.Village;
import com.apiecommerce.tokoto.location.village.VillageRepository;
import com.apiecommerce.tokoto.location.village.VillageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

import java.util.List;

import static com.apiecommerce.tokoto.user.Role.*;

@SpringBootApplication
@EnableJdbcHttpSession
@EnableCaching
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class TokotoApplication {

	 @Autowired
	 private GenderService genderService;

	 @Autowired
	 private ProvinceService provinceService;

	 @Autowired
	 private RegencyService regencyService;

	 @Autowired
	 private DistrictService districtService;

	 @Autowired
	 private VillageService villageService;

	public static void main(String[] args) {
		SpringApplication.run(TokotoApplication.class, args);
	}

//	 @Bean
//	 public CommandLineRunner commandLineRunner(
//	 		AuthenticationService service
//	 ) {
//	 	return args -> {
//	 		List<Gender> validGenders = genderService.getAllGenders();
//	 		if (!validGenders.isEmpty()) {
//	 			Gender genderLaki = genderService.getGenderByName("Laki-laki");
//	 			Gender genderPerempuan = genderService.getGenderByName("Perempuan");
//
//				 Province jawaTimur = provinceService.findByName("JAWA TIMUR");
//				 Regency surabaya = regencyService.findByNames(jawaTimur.getName(), "KOTA SURABAYA");
//				 District mulyorejoDis = districtService.findDistrictByNames(jawaTimur.getName(), surabaya.getName(), "MULYOREJO");
//				 Village mulyorejoVil = villageService.getVillageByDistrictAndNames(jawaTimur.getName(), surabaya.getName(), mulyorejoDis.getName(), "MULYOREJO");
//
//				Regency sidoarjo = regencyService.findByNames(jawaTimur.getName(), "KABUPATEN SIDOARJO");
//				District gedangan = districtService.findDistrictByNames(jawaTimur.getName(), sidoarjo.getName(), "GEDANGAN");
//				Village wedi = villageService.getVillageByDistrictAndNames(jawaTimur.getName(), sidoarjo.getName(), gedangan.getName(), "WEDI");
//
//				Province jakarta = provinceService.findByName("DKI JAKARTA");
//				Regency jakartaTimur = regencyService.findByNames(jakarta.getName(), "KOTA JAKARTA TIMUR");
//				District jatinegara = districtService.findDistrictByNames(jakarta.getName(), jakartaTimur.getName(), "JATINEGARA");
//				Village kampungMelayu = villageService.getVillageByDistrictAndNames(jakarta.getName(), jakartaTimur.getName(), jatinegara.getName(), "KAMPUNG MELAYU");
//
//	 			if (genderLaki != null && genderPerempuan != null) {
//	 				var admin = RegisterRequest.builder()
//	 						.firstname("Dearly")
//	 						.lastname("Febriano")
//	 						.email("dearlyfebriano08@gmail.com")
//	 						.password("dearlyfebriano08")
//	 						.phone("083854436555")
//	 						.role(ADMIN)
//	 						.gender(genderLaki)
//							.provinceName(jawaTimur)
//							.regencyName(surabaya)
//							.districtName(mulyorejoDis)
//							.villageName(mulyorejoVil)
//	 						.build();
//	 				System.out.println("Admin token: " + service.register(admin).getAccessToken());
//
//	 				var manager = RegisterRequest.builder()
//	 						.firstname("Arvan")
//	 						.lastname("Gibran")
//	 						.email("arvangibran1@gmail.com")
//	 						.password("arvangibran")
//	 						.phone("083812387899")
//	 						.role(MANAGER)
//	 						.gender(genderPerempuan)
//							.provinceName(jawaTimur)
//							.regencyName(sidoarjo)
//							.districtName(gedangan)
//							.villageName(wedi)
//	 						.build();
//	 				System.out.println("Manager token: " + service.register(manager).getAccessToken());
//
//	 				var client = RegisterRequest.builder()
//	 						.firstname("Client")
//	 						.lastname("Ctest")
//	 						.email("ctest@gmail.com")
//	 						.password("test")
//	 						.phone("082362561321")
//	 						.role(CLIENT)
//	 						.gender(genderLaki)
//							.provinceName(jakarta)
//							.regencyName(jakartaTimur)
//							.districtName(jatinegara)
//							.villageName(kampungMelayu)
//	 						.build();
//	 				System.out.println("Client token: " + service.register(client).getAccessToken());
//	 			} else {
//	 				System.out.println("Laki-laki and/or Perempuan genders not found in the database.");
//	 			}
//	 		} else {
//	 			System.out.println("No Valid Genders found in the database.");
//	 		}
//	 	};
//	 }
}
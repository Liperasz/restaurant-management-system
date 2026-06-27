package com.camaradacamarao.api.config;

import com.camaradacamarao.api.model.Ingredient;
import com.camaradacamarao.api.model.MenuItem;
import com.camaradacamarao.api.model.User;
import com.camaradacamarao.api.model.enums.Gender;
import com.camaradacamarao.api.model.enums.Role;
import com.camaradacamarao.api.repository.IngredientRepository;
import com.camaradacamarao.api.repository.MenuItemRepository;
import com.camaradacamarao.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_PASSWORD:admin}")
    private String adminPassword;

    public DataSeeder(UserRepository userRepository,
                      MenuItemRepository menuItemRepository,
                      IngredientRepository ingredientRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.ingredientRepository = ingredientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedIngredientsAndMenuItems();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@camaradacamarao.com")) {
            User admin = new User();
            admin.setName("Administrador Padrão");
            admin.setEmail("admin@camaradacamarao.com");
            admin.setCpf("00000000000");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMINISTRATOR);
            admin.setGender(Gender.OTHER);
            admin.setBirthDate(LocalDate.of(2000, 1, 1));
            admin.setPhone("00000000000");
            userRepository.save(admin);
        }
        
        if (!userRepository.existsByEmail("attendant@camaradacamarao.com")) {
            User attendant = new User();
            attendant.setName("Atendente Padrão");
            attendant.setEmail("attendant@camaradacamarao.com");
            attendant.setCpf("11111111111");
            attendant.setPassword(passwordEncoder.encode("attendant"));
            attendant.setRole(Role.ATTENDANT);
            attendant.setGender(Gender.OTHER);
            attendant.setBirthDate(LocalDate.of(2000, 1, 1));
            attendant.setPhone("11111111111");
            userRepository.save(attendant);
        }
        
        if (!userRepository.existsByEmail("customer@camaradacamarao.com")) {
            User customer = new User();
            customer.setName("Cliente Padrão");
            customer.setEmail("customer@camaradacamarao.com");
            customer.setCpf("22222222222");
            customer.setPassword(passwordEncoder.encode("customer"));
            customer.setRole(Role.CUSTOMER);
            customer.setGender(Gender.OTHER);
            customer.setBirthDate(LocalDate.of(2000, 1, 1));
            customer.setPhone("22222222222");
            userRepository.save(customer);
        }
    }

    private void seedIngredientsAndMenuItems() {
        if (ingredientRepository.count() == 0 && menuItemRepository.count() == 0) {
            Ingredient camarao = new Ingredient();
            camarao.setName("Camarão");
            camarao.setUnit("kg");
            camarao = ingredientRepository.save(camarao);

            Ingredient arroz = new Ingredient();
            arroz.setName("Arroz");
            arroz.setUnit("kg");
            arroz = ingredientRepository.save(arroz);

            Ingredient alho = new Ingredient();
            alho.setName("Alho");
            alho.setUnit("kg");
            alho = ingredientRepository.save(alho);

            MenuItem prato1 = new MenuItem();
            prato1.setName("Camarão ao Alho e Óleo");
            prato1.setPrice(new BigDecimal("55.90"));
            prato1.setDescription("Delicioso camarão frito no alho e óleo.");
            prato1.setActive(true);
            prato1.setIngredients(List.of(camarao, alho));
            menuItemRepository.save(prato1);

            MenuItem prato2 = new MenuItem();
            prato2.setName("Risoto de Camarão");
            prato2.setPrice(new BigDecimal("68.50"));
            prato2.setDescription("Risoto cremoso com camarões selecionados.");
            prato2.setActive(true);
            prato2.setIngredients(List.of(camarao, arroz));
            menuItemRepository.save(prato2);
        }
    }
}

package com.example.garage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * ══════════════════════════════════════════════════════════════
 *  DATA INITIALIZER — Street Racer
 * ══════════════════════════════════════════════════════════════
 *  4 Concessionnaires, puissance croissante :
 *  [1] Street Imports     60-140 CH  abordables
 *  [2] Touge Selection   140-280 CH  sportives classiques
 *  [3] JDM Performance   260-380 CH  icônes JDM
 *  [4] Legend Motorsport 380-700 CH  machines de course
 *  Inspiré Gran Turismo 2 & 4.
 * ══════════════════════════════════════════════════════════════
 */
@Configuration
public class DataInitializer {

    private static final AspirationType N = AspirationType.NATURAL;
    private static final AspirationType T = AspirationType.TURBO;

    @Bean
    public CommandLineRunner initData(
            UserRepository       userRepo,
            DealershipRepository dealerRepo,
            CarRepository        carRepo,
            ParkingRepository    parkingRepo,
            RacersRepository     racersRepo,
            RaceRepository       raceRepo,
            SrcTeamRepository    srcTeamRepo,
            CarUpgradeRepository upgradeRepo) {

        return args -> {

            // 1. JOUEUR
            if (userRepo.count() == 0) {
                User p = new User();
                p.setUsername("RacingLegend_01");
                p.setCredits(15000);
                p.setLevel(1);
                Garage g = new Garage();
                p.setGarage(g);
                userRepo.save(p);
                System.out.println("[INIT] Joueur cree.");
            }

            // 2. CONCESSIONNAIRES
            if (dealerRepo.count() == 0) {

                // ── TIER 1 — Street Imports ──────────────────────
                Dealership d1 = newDealer("Street Imports");
                lc(d1,"Honda Civic EK3 1.5i",              90,1080, 8500,N,"Sport",1.12);
                lc(d1,"Honda Civic EK9 Type R",           185,1050,22000,N,"Sport",1.32);
                lc(d1,"Honda Civic EG6 SiR II",           160,1040,16500,N,"Sport",1.28);
                lc(d1,"Honda Integra DC2 Type R",         197,1090,25000,N,"Sport",1.35);
                lc(d1,"Honda Integra DB8 Type R",         180,1100,19500,N,"Sport",1.30);
                lc(d1,"Honda CR-X SiR",                   150, 980,14000,N,"Sport",1.25);
                lc(d1,"Honda Beat",                        64, 760, 6500,N,"Sport",1.20);
                lc(d1,"Honda City Turbo II",               110, 910,10000,T,"Sport",1.15);
                lc(d1,"Honda Today",                       56, 720, 4500,N,"Sport",1.08);
                lc(d1,"Honda Prelude Si 4WS",             160,1165,16000,N,"Sport",1.22);
                lc(d1,"Mazda MX-5 Miata NA",              100, 940,12000,N,"Sport",1.38);
                lc(d1,"Mazda MX-5 Miata NB",              140, 975,17000,N,"Sport",1.40);
                lc(d1,"Mazda 323F Astina",                105,1080,10500,N,"Sport",1.18);
                lc(d1,"Mazda 323 GT-R",                   140,1065,13000,T,"Sport",1.22);
                lc(d1,"Mazda Familia GT-X",               140,1150,15000,T,"Sport",1.20);
                lc(d1,"Mazda AZ-1",                        64, 720, 8000,T,"Sport",1.32);
                lc(d1,"Mazda Eunos Roadster S-Special",   128, 950,14500,N,"Sport",1.35);
                lc(d1,"Mitsubishi Mirage Asti",            78, 880, 6000,N,"Sport",1.10);
                lc(d1,"Mitsubishi Colt 1.5 Sport",         95, 980, 8000,N,"Sport",1.12);
                lc(d1,"Mitsubishi FTO GS",                170,1180,18000,N,"Sport",1.25);
                lc(d1,"Mitsubishi Eclipse 1G GSX",        195,1310,21000,T,"Sport",1.22);
                lc(d1,"Mitsubishi Eclipse 2G GS-T",       215,1260,24000,T,"Sport",1.24);
                lc(d1,"Subaru Justy GLI",                  72, 840, 5000,N,"Sport",1.08);
                lc(d1,"Subaru Vivio RX-R",                 64, 690, 7500,T,"Sport",1.28);
                lc(d1,"Subaru Impreza 1.5i",              100,1095,11000,N,"Sport",1.15);
                lc(d1,"Subaru Impreza Sport Wagon",       125,1230,14000,N,"Sport",1.18);
                lc(d1,"Daihatsu Mira TR-XX Avanzato",      64, 660, 6000,T,"Sport",1.25);
                lc(d1,"Suzuki Alto Works RS/Z",             64, 680, 5500,T,"Sport",1.22);
                lc(d1,"Suzuki Cappuccino",                  64, 720, 7000,T,"Sport",1.30);
                lc(d1,"Suzuki Swift Sport ZC31S",          125,1035,13000,N,"Sport",1.20);
                lc(d1,"Toyota Starlet EP82 GT Turbo",     135, 930,13000,T,"Sport",1.20);
                lc(d1,"Toyota Starlet EP71 Si-Limited",   105, 900,10000,T,"Sport",1.18);
                lc(d1,"Toyota Corolla FX GT",             128,1010,12000,N,"Sport",1.18);
                lc(d1,"Toyota Corolla Levin AE101",       165,1040,16000,N,"Sport",1.28);
                lc(d1,"Toyota Corolla Levin BZ-R",        165,1040,17000,N,"Sport",1.30);
                lc(d1,"Toyota MR2 AW11 SC",               145, 990,20000,T,"Sport",1.30);
                lc(d1,"Toyota MR2 SW20 GT-S",             245,1280,28000,T,"Sport",1.26);
                lc(d1,"Toyota MR-S ZZW30",                140,1020,32000,N,"Sport",1.30);
                lc(d1,"Toyota Cynos 1.3",                  88, 960, 7000,N,"Sport",1.10);
                lc(d1,"Nissan March Super Turbo",          110, 800, 9000,T,"Sport",1.20);
                lc(d1,"Nissan Micra K11 1.3",               75, 870, 5500,N,"Sport",1.08);
                lc(d1,"Nissan Pulsar GTi-R",               230,1220,26000,T,"Sport",1.30);
                lc(d1,"Nissan Bluebird SSS Attesa",        180,1290,22000,T,"Sport",1.20);
                lc(d1,"Isuzu Piazza Turbo",                140,1210,12000,T,"Sport",1.15);
                lc(d1,"Honda Accord Type R",               212,1320,22000,N,"Sport",1.25);
                lc(d1,"Honda Ballade Sports CRX",          130, 940,11000,N,"Sport",1.22);
                lc(d1,"Mazda Lantis Type R",               170,1110,17500,N,"Sport",1.24);
                lc(d1,"Mitsubishi GTO MR",                 280,1660,32000,T,"Sport",1.18);
                lc(d1,"Isuzu Impulse RS",                  170,1210,16000,T,"Sport",1.18);
                lc(d1,"Mitsubishi Galant VR-4 E39A",       240,1480,36000,T,"Sport",1.22);
                dealerRepo.save(d1);
                System.out.println("[INIT] Dealer 1 - Street Imports: 50 voitures.");

                // ── TIER 2 — Touge Selection ─────────────────────
                Dealership d2 = newDealer("Touge Selection");
                lc(d2,"Toyota AE86 Sprinter Trueno GT-APEX",128, 925,18000,N,"Sport",1.45);
                lc(d2,"Toyota AE86 Corolla Levin GT-APEX",  128, 940,17500,N,"Sport",1.44);
                lc(d2,"Toyota Celica GT-Four ST185",         235,1380,32000,T,"Sport",1.28);
                lc(d2,"Toyota Celica GT-Four ST205",         255,1460,38000,T,"Sport",1.30);
                lc(d2,"Toyota Celica SS-II ST202",           175,1180,24000,N,"Sport",1.25);
                lc(d2,"Toyota MR2 SW20 GT",                  225,1260,34000,T,"Sport",1.28);
                lc(d2,"Toyota Altezza RS200",                210,1240,30000,N,"Sport",1.30);
                lc(d2,"Toyota Altezza SXE10 Gita",           200,1320,27000,N,"Sport",1.28);
                lc(d2,"Toyota Soarer 2.5 GT Twin Turbo",     280,1670,40000,T,"Sport",1.18);
                lc(d2,"Toyota Supra A80 2.5 Twin Turbo",     280,1510,48000,T,"Sport",1.25);
                lc(d2,"Toyota Chaser Tourer V JZX100",       280,1550,52000,T,"Sport",1.25);
                lc(d2,"Toyota Mark II Tourer V JZX100",      280,1560,50000,T,"Sport",1.24);
                lc(d2,"Nissan Silvia S13 K's",               205,1200,28000,T,"Sport",1.20);
                lc(d2,"Nissan Silvia S14 K's",               220,1260,32000,T,"Sport",1.22);
                lc(d2,"Nissan Silvia S15 Spec-R",            250,1240,40000,T,"Sport",1.25);
                lc(d2,"Nissan 180SX Type-X",                 205,1220,26000,T,"Sport",1.22);
                lc(d2,"Nissan Fairlady Z 300ZX TT",          300,1600,45000,T,"Sport",1.20);
                lc(d2,"Nissan Fairlady Z 240ZG",             160,1080,28000,N,"Sport",1.25);
                lc(d2,"Nissan Skyline GTS-t R32",            210,1360,30000,T,"Sport",1.22);
                lc(d2,"Nissan Skyline GTS-25t R33",          230,1400,35000,T,"Sport",1.24);
                lc(d2,"Nissan Fairlady Z S30 432R",          260,1060,68000,N,"Sport",1.38);
                lc(d2,"Nissan Primera 2.0Te",                150,1200,18000,N,"Sport",1.18);
                lc(d2,"Honda NSX-T",                         274,1370,65000,N,"Sport",1.38);
                lc(d2,"Honda NSX Type R NA1",                280,1270,85000,N,"Semi-Slick",1.48);
                lc(d2,"Honda S2000 AP1",                     250,1240,48000,N,"Sport",1.38);
                lc(d2,"Honda S2000 AP2",                     242,1250,45000,N,"Sport",1.36);
                lc(d2,"Honda Integra Type R DC5",            220,1120,36000,N,"Sport",1.38);
                lc(d2,"Honda Prelude Type S BB6",            220,1300,30000,N,"Sport",1.22);
                lc(d2,"Mazda RX-7 FC3S Turbo II",           185,1260,32000,T,"Sport",1.30);
                lc(d2,"Mazda RX-7 FC3S Infini III",         200,1270,36000,T,"Sport",1.32);
                lc(d2,"Mazda RX-7 FD3S Type R",             255,1250,55000,T,"Sport",1.36);
                lc(d2,"Mazda RX-7 FD3S R-Spec",             280,1240,65000,T,"Sport",1.38);
                lc(d2,"Mazda MX-5 Miata RS NB8C",           155, 995,22000,N,"Sport",1.42);
                lc(d2,"Mazda Cosmo Type E",                  230,1390,34000,T,"Sport",1.22);
                lc(d2,"Mazda Speed Atenza",                  260,1520,55000,T,"Sport",1.28);
                lc(d2,"Mitsubishi Lancer Evo I GSR",         250,1150,38000,T,"Sport",1.38);
                lc(d2,"Mitsubishi Lancer Evo II GSR",        260,1170,42000,T,"Sport",1.40);
                lc(d2,"Mitsubishi Lancer Evo III GSR",       270,1175,46000,T,"Sport",1.42);
                lc(d2,"Mitsubishi Lancer Evo IV GSR",        280,1260,50000,T,"Sport",1.44);
                lc(d2,"Mitsubishi Lancer Evo V GSR",         280,1260,54000,T,"Sport",1.46);
                lc(d2,"Mitsubishi Starion GSR-VR",           200,1340,26000,T,"Sport",1.20);
                lc(d2,"Subaru Impreza WRX GC8",              240,1185,38000,T,"Sport",1.35);
                lc(d2,"Subaru Impreza WRX STi Version I",    250,1200,44000,T,"Sport",1.38);
                lc(d2,"Subaru Impreza WRX STi Version III",  280,1240,52000,T,"Sport",1.42);
                lc(d2,"Subaru Impreza WRX STi Version V",    280,1260,56000,T,"Sport",1.44);
                lc(d2,"Subaru Legacy B4 RSK",                280,1340,42000,T,"Sport",1.30);
                lc(d2,"Subaru Legacy 2.0 GT",                280,1340,29000,T,"Sport",1.28);
                lc(d2,"Suzuki Escudo Pikes Peak",            220,1170,38000,T,"Semi-Slick",1.35);
                lc(d2,"Mitsubishi GTO Twin Turbo MR",        320,1740,65000,T,"Sport",1.22);
                lc(d2,"Mitsubishi Eclipse GT Race Car",      320,1210,55000,T,"Sport",1.32);
                dealerRepo.save(d2);
                System.out.println("[INIT] Dealer 2 - Touge Selection: 50 voitures.");

                // ── TIER 3 — JDM Performance ─────────────────────
                Dealership d3 = newDealer("JDM Performance");
                lc(d3,"Nissan Skyline GT-R R32",               280,1430, 85000,T,"Semi-Slick",1.42);
                lc(d3,"Nissan Skyline GT-R R32 V-Spec",        280,1430, 95000,T,"Semi-Slick",1.45);
                lc(d3,"Nissan Skyline GT-R R33",               280,1530, 90000,T,"Semi-Slick",1.42);
                lc(d3,"Nissan Skyline GT-R R33 V-Spec",        280,1530,100000,T,"Semi-Slick",1.45);
                lc(d3,"Nissan Skyline GT-R R34",               330,1540,120000,T,"Semi-Slick",1.48);
                lc(d3,"Nissan Skyline GT-R R34 V-Spec",        330,1540,135000,T,"Semi-Slick",1.52);
                lc(d3,"Nissan Skyline GT-R R34 V-Spec II",     330,1530,145000,T,"Semi-Slick",1.54);
                lc(d3,"Nissan Skyline GT-R R34 M-Spec Nur",    330,1550,155000,T,"Semi-Slick",1.50);
                lc(d3,"Nissan 350Z Nismo S-Tune",              306,1420, 72000,N,"Semi-Slick",1.38);
                lc(d3,"Nissan 350Z Track",                     300,1380, 62000,N,"Semi-Slick",1.36);
                lc(d3,"Nissan Fairlady Z 350Z Version S",      300,1370, 58000,N,"Semi-Slick",1.34);
                lc(d3,"Nissan Silvia S15 Spec-S Aero",         250,1250, 42000,T,"Sport",1.26);
                lc(d3,"Mazda RX-7 FD3S RZ",                    280,1230, 75000,T,"Semi-Slick",1.42);
                lc(d3,"Mazda RX-7 FD3S Spirit R Type A",       280,1220, 90000,T,"Semi-Slick",1.48);
                lc(d3,"Mazda RX-7 FD3S Spirit R Type B",       280,1230, 88000,T,"Semi-Slick",1.46);
                lc(d3,"Mazda RX-8 Spirit R",                   232,1310, 55000,N,"Semi-Slick",1.40);
                lc(d3,"Mazda Speed Roadster",                  178, 975, 65000,N,"Semi-Slick",1.58);
                lc(d3,"Toyota Supra RZ A80",                   280,1520, 80000,T,"Semi-Slick",1.35);
                lc(d3,"Toyota Supra RZ S A80",                 280,1510, 85000,T,"Semi-Slick",1.38);
                lc(d3,"Toyota Celica GT-FOUR RC ST185",        241,1390, 52000,T,"Sport",1.32);
                lc(d3,"Toyota Aristo 3.0V",                    280,1750, 58000,T,"Sport",1.20);
                lc(d3,"Honda NSX-R NA1",                       280,1230,120000,N,"Slick",1.55);
                lc(d3,"Honda NSX-R Concept NA2",               290,1260,135000,N,"Slick",1.58);
                lc(d3,"Honda S2000 Club Racer",                250,1175, 65000,N,"Semi-Slick",1.45);
                lc(d3,"Mitsubishi Lancer Evo VI GSR",          280,1360, 70000,T,"Semi-Slick",1.48);
                lc(d3,"Mitsubishi Lancer Evo VI T.Makinen Ed.",280,1350, 82000,T,"Semi-Slick",1.52);
                lc(d3,"Mitsubishi Lancer Evo VII GSR",         280,1410, 75000,T,"Semi-Slick",1.50);
                lc(d3,"Mitsubishi Lancer Evo VII RS",          280,1380, 78000,T,"Semi-Slick",1.52);
                lc(d3,"Mitsubishi Lancer Evo VIII GSR",        280,1400, 82000,T,"Semi-Slick",1.54);
                lc(d3,"Mitsubishi Lancer Evo VIII MR GSR",     280,1390, 88000,T,"Semi-Slick",1.56);
                lc(d3,"Mitsubishi Lancer Evo IX GSR",          280,1410, 88000,T,"Semi-Slick",1.56);
                lc(d3,"Mitsubishi Lancer Evo IX MR GSR",       280,1400, 95000,T,"Semi-Slick",1.58);
                lc(d3,"Subaru Impreza WRX STi Version VI",     280,1260, 62000,T,"Semi-Slick",1.46);
                lc(d3,"Subaru Impreza WRX STi spec C",         300,1280, 78000,T,"Semi-Slick",1.50);
                lc(d3,"Subaru Impreza WRX STi S202",           320,1270, 98000,T,"Semi-Slick",1.54);
                lc(d3,"Subaru Impreza WRX STi S203",           320,1280,108000,T,"Semi-Slick",1.55);
                lc(d3,"Subaru Impreza WRX STi S204",           320,1280,115000,T,"Semi-Slick",1.56);
                lc(d3,"Subaru Legacy B4 3.0R",                 260,1470, 68000,N,"Sport",1.30);
                lc(d3,"Subaru Forester STi",                   265,1500, 62000,T,"Sport",1.28);
                lc(d3,"Subaru Impreza WRX STi RA",             300,1250, 89000,T,"Semi-Slick",1.52);
                lc(d3,"Nissan Skyline GT-R R32 Group A",       420,1270,250000,T,"Slick",1.68);
                lc(d3,"Mitsubishi Lancer Evo VI Group A",      380,1210,185000,T,"Slick",1.72);
                lc(d3,"Toyota Celica GT-Four Race Car",        380,1210,195000,T,"Slick",1.68);
                lc(d3,"Honda Civic Type R EK9 Race Mod",       220, 990,105000,N,"Slick",1.70);
                lc(d3,"Toyota Sprinter Trueno AE86 N2",        175, 890, 88000,N,"Slick",1.72);
                lc(d3,"Nissan Fairlady Z 432 Race",            200,1040, 85000,N,"Slick",1.55);
                lc(d3,"Honda Integra Type R Touring Car",      310,1000,165000,N,"Slick",1.72);
                lc(d3,"Honda Accord Touring Car",              300,1200,150000,N,"Slick",1.65);
                lc(d3,"Nissan Silvia S15 Race Car",            350,1150,160000,T,"Slick",1.65);
                lc(d3,"Toyota MR2 GT-S Race",                  280,1080,120000,T,"Slick",1.60);
                dealerRepo.save(d3);
                System.out.println("[INIT] Dealer 3 - JDM Performance: 50 voitures.");

                // ── TIER 4 — Legend Motorsport ────────────────────
                Dealership d4 = newDealer("Legend Motorsport");
                lc(d4,"Nissan Skyline GT-R R34 Z-Tune",       493,1450,380000,T,"Slick",1.72);
                lc(d4,"Nissan Skyline GT-R R34 N1",           380,1530,220000,T,"Slick",1.62);
                lc(d4,"Nissan GT-R Proto",                    485,1740,280000,T,"Slick",1.60);
                lc(d4,"Mine's BNR34 Skyline GT-R",            500,1480,450000,T,"Slick",1.75);
                lc(d4,"Nismo 400R",                           400,1430,285000,T,"Slick",1.68);
                lc(d4,"Top Secret GT-R",                      560,1510,480000,T,"Slick",1.72);
                lc(d4,"Nissan XANAVI Nismo GT-R JGTC",        450,1100,320000,T,"Slick",1.78);
                lc(d4,"Nissan R390 GT1",                      550, 950,480000,T,"Slick",1.86);
                lc(d4,"Nissan R33 GT-R LM",                   600, 930,520000,T,"Slick",1.82);
                lc(d4,"Nissan R89C",                          580, 900,495000,N,"Slick",1.84);
                lc(d4,"Nissan 350Z Race Car",                 380,1250,180000,N,"Slick",1.64);
                lc(d4,"Toyota Supra GT TOM'S JGTC",          480,1050,340000,T,"Slick",1.80);
                lc(d4,"Toyota Supra RZ TRD 3000GT",          450,1490,280000,T,"Slick",1.58);
                lc(d4,"Toyota GT-One TS020",                  600,1000,500000,N,"Slick",1.88);
                lc(d4,"Toyota Altezza Race SUPER GT",         400,1080,220000,N,"Slick",1.75);
                lc(d4,"Toyota Celica GT-Four Rally",          380,1310,195000,T,"Slick",1.65);
                lc(d4,"Toyota GT-Four ST185 Rally Car",       300,1150,175000,T,"Slick",1.70);
                lc(d4,"Toyota Sprinter Trueno AE86 Race",     200, 875, 95000,N,"Slick",1.75);
                lc(d4,"Honda NSX GT500 Castrol JGTC",        480,1050,315000,N,"Slick",1.80);
                lc(d4,"Honda NSX-R GT3 LM",                  490,1180,320000,N,"Slick",1.82);
                lc(d4,"Honda S2000 LM Race",                  380,1095,200000,N,"Slick",1.74);
                lc(d4,"Honda Integra TC",                     310,1000,165000,N,"Slick",1.72);
                lc(d4,"Mazda 787B",                           700, 925,600000,N,"Slick",1.85);
                lc(d4,"Mazda RX-7 GT JGTC RE Amemiya",       420,1000,295000,T,"Slick",1.76);
                lc(d4,"Mazda RX-7 FD3S Racing",               350,1120,185000,T,"Slick",1.65);
                lc(d4,"RE Amemiya FD3S Super G",              450,1180,320000,T,"Slick",1.74);
                lc(d4,"Amemiya AD Facer FD3S",               400,1200,260000,T,"Slick",1.70);
                lc(d4,"Mazda 323 GT-R Group A",               300,1060,155000,T,"Slick",1.70);
                lc(d4,"Mazda Speed Roadster Race",            220, 950,115000,N,"Slick",1.68);
                lc(d4,"Mitsubishi Lancer Evo VI Group A",     380,1210,185000,T,"Slick",1.72);
                lc(d4,"Mitsubishi Lancer Evo IX FQ400",       400,1390,198000,T,"Slick",1.70);
                lc(d4,"Mitsubishi FTO LM Race Car",           300,1060,130000,N,"Slick",1.68);
                lc(d4,"Mitsubishi 3000GT TT Race",            380,1420,175000,T,"Slick",1.58);
                lc(d4,"Subaru Impreza WRC 1997",              300,1250,195000,T,"Slick",1.75);
                lc(d4,"Subaru Impreza WRC 2001",              300,1235,200000,T,"Slick",1.78);
                lc(d4,"Subaru Impreza WRX STi S400",          400,1260,250000,T,"Slick",1.68);
                lc(d4,"Subaru Impreza Rally Car 555",         300,1225,190000,T,"Slick",1.78);
                lc(d4,"Subaru Impreza Race Car",              420,1220,245000,T,"Slick",1.72);
                lc(d4,"Subaru Impreza WRX STi 22B STi",      280,1220,125000,T,"Semi-Slick",1.62);
                lc(d4,"HKS Time Attack Evo",                  600,1320,550000,T,"Slick",1.80);
                lc(d4,"Suzuki Escudo Dirt Trial Car",         500,1100,350000,T,"Slick",1.78);
                lc(d4,"Nissan Skyline GT-R R33 V-Spec N1",    400,1490,240000,T,"Slick",1.65);
                lc(d4,"Nissan Skyline GT-R R32 N1",           320,1430,175000,T,"Slick",1.62);
                lc(d4,"Honda NSX GT500 Raybrig JGTC",        440,1020,310000,N,"Slick",1.78);
                lc(d4,"Mazda RX-7 LM Race Special",          420,1050,310000,T,"Slick",1.78);
                lc(d4,"Toyota Supra GT500 WoodOne JGTC",     460,1060,330000,T,"Slick",1.80);
                lc(d4,"Subaru Impreza STi S400 Time Attack",  450,1245,380000,T,"Slick",1.76);
                lc(d4,"Mitsubishi Lancer Evo IX TC",          360,1340,195000,T,"Slick",1.68);
                lc(d4,"Nissan 350Z Nismo Z-Tune Concept",     400,1380,290000,N,"Slick",1.65);
                lc(d4,"Nissan GT-R R35 Proto Race Spec",      550,1720,495000,T,"Slick",1.78);
                dealerRepo.save(d4);
                System.out.println("[INIT] Dealer 4 - Legend Motorsport: 50 voitures.");
            }

            // 3. CIRCUITS + 4. PARKINGS — tout dans un seul bloc pour éviter les nulls SQLite
            if (raceRepo.count() == 0 && parkingRepo.count() == 0 && racersRepo.count() == 0) {
                // Sauvegarde les circuits un par un et conserve les références directement
                // Segments : S=straight(longueur), C=corner(angle 30=rapide 150=lent)
                // Format : S:longueur,C:angle,S:longueur,C:angle,...
                Race rAkihabara = raceRepo.save(mkRace("Akihabara Back Street",
                    "S:600,C:30,S:800,C:45,S:500,C:30,S:400,C:60"));
                Race rWangan    = raceRepo.save(mkRace("Wangan Mile",
                    "S:1200,C:20,S:1500,C:15,S:1000,C:25,S:800,C:20"));
                Race rBayshore  = raceRepo.save(mkRace("Bayshore Route",
                    "S:700,C:45,S:400,C:90,S:600,C:60,S:500,C:45,S:300,C:75"));
                Race rC1        = raceRepo.save(mkRace("C1 Inner Loop",
                    "S:300,C:90,S:200,C:120,S:250,C:90,S:200,C:105,S:150,C:90,S:300,C:75"));
                Race rHakone    = raceRepo.save(mkRace("Hakone Mountain Pass",
                    "S:150,C:120,S:100,C:135,S:200,C:90,S:80,C:150,S:120,C:120,S:90,C:105"));
                Race rYokohama  = raceRepo.save(mkRace("Yokohama Docks",
                    "S:500,C:60,S:600,C:45,S:400,C:75,S:350,C:60,S:500,C:45"));
                Race rShuto     = raceRepo.save(mkRace("Shuto Expressway",
                    "S:900,C:30,S:700,C:45,S:800,C:30,S:600,C:45,S:400,C:60"));
                Race rTsukuba   = raceRepo.save(mkRace("Tsukuba Sprint",
                    "S:400,C:75,S:300,C:90,S:350,C:60,S:250,C:105,S:400,C:75,S:200,C:90"));
                Race rNikko     = raceRepo.save(mkRace("Nikko Circuit",
                    "S:300,C:90,S:250,C:105,S:350,C:75,S:200,C:120,S:300,C:90,S:250,C:75"));
                Race rFuji      = raceRepo.save(mkRace("Fuji Speedway Short",
                    "S:800,C:45,S:500,C:60,S:700,C:30,S:400,C:75,S:600,C:45"));

                // ══ 10 COLS DE MONTAGNE — Beaucoup de virages ══
                Race rIrohazaka = raceRepo.save(mkRace("Irohazaka Pass",
                    "S:80,C:120,S:60,C:135,S:70,C:105,S:50,C:150,S:80,C:120,S:60,C:135,S:70,C:120"));
                Race rRoppongi  = raceRepo.save(mkRace("Roppongi Hills Pass",
                    "S:100,C:105,S:80,C:120,S:90,C:90,S:70,C:135,S:100,C:105,S:80,C:120"));
                Race rUsui      = raceRepo.save(mkRace("Usui Mountain Road",
                    "S:120,C:90,S:80,C:120,S:100,C:105,S:60,C:150,S:90,C:90,S:70,C:120,S:80,C:105"));
                Race rHaruna    = raceRepo.save(mkRace("Haruna Lake Circuit",
                    "S:90,C:120,S:70,C:105,S:100,C:90,S:80,C:135,S:90,C:120,S:60,C:150,S:80,C:90"));
                Race rAkagi     = raceRepo.save(mkRace("Mount Akagi Pass",
                    "S:150,C:90,S:100,C:120,S:80,C:105,S:60,C:135,S:90,C:90,S:70,C:120"));
                Race rNasu      = raceRepo.save(mkRace("Nasu Highland Pass",
                    "S:200,C:75,S:100,C:105,S:150,C:90,S:80,C:120,S:120,C:75,S:90,C:105"));
                Race rTateyama  = raceRepo.save(mkRace("Tateyama Alpine Route",
                    "S:60,C:135,S:50,C:150,S:70,C:120,S:80,C:105,S:60,C:135,S:50,C:150,S:70,C:120"));
                Race rHakuba    = raceRepo.save(mkRace("Hakuba Valley Road",
                    "S:120,C:90,S:80,C:120,S:100,C:105,S:70,C:135,S:90,C:90,S:80,C:120,S:60,C:105"));
                Race rToyako    = raceRepo.save(mkRace("Toyako Panorama Pass",
                    "S:180,C:75,S:120,C:90,S:100,C:105,S:80,C:120,S:150,C:75,S:100,C:90"));
                Race rAsama     = raceRepo.save(mkRace("Mount Asama Circuit",
                    "S:100,C:120,S:80,C:135,S:70,C:105,S:90,C:120,S:80,C:135,S:60,C:150,S:70,C:105"));
                System.out.println("[INIT] 20 circuits crees (10 standard + 10 cols de montagne).");

                // Voitures adversaires
                Car ae86  = mkCar("Toyota AE86 Trueno GT-APEX", 128, 925, N, "Sport",      1.45);
                Car mx5   = mkCar("Mazda MX-5 NA",              100, 940, N, "Sport",      1.38);
                Car crx   = mkCar("Honda CR-X SiR",             150, 980, N, "Sport",      1.25);
                Car ek9   = mkCar("Honda Civic EK9 Type R",     185,1050, N, "Sport",      1.32);
                Car star  = mkCar("Toyota Starlet GT Turbo",    135, 930, T, "Sport",      1.20);
                Car s13   = mkCar("Nissan Silvia S13 K's",      205,1200, T, "Sport",      1.20);
                Car s14   = mkCar("Nissan Silvia S14 K's",      220,1260, T, "Sport",      1.22);
                Car fc3s  = mkCar("Mazda RX-7 FC3S Turbo II",   185,1260, T, "Sport",      1.30);
                Car evo4  = mkCar("Mitsubishi Lancer Evo IV",   280,1260, T, "Sport",      1.44);
                Car alt   = mkCar("Toyota Altezza RS200",       210,1240, N, "Sport",      1.30);
                Car nsx   = mkCar("Honda NSX-T",                274,1370, N, "Sport",      1.38);
                Car r34   = mkCar("Nissan Skyline GT-R R34",    330,1540, T, "Semi-Slick", 1.48);
                Car fd3s  = mkCar("Mazda RX-7 FD3S Spirit R",   280,1220, T, "Semi-Slick", 1.48);
                Car supra = mkCar("Toyota Supra RZ A80",        280,1520, T, "Semi-Slick", 1.35);
                Car evo9  = mkCar("Mitsubishi Lancer Evo IX",   280,1410, T, "Semi-Slick", 1.56);
                Car sti5  = mkCar("Subaru Impreza STi V",       280,1260, T, "Semi-Slick", 1.44);
                Car nsxr  = mkCar("Honda NSX-R NA1",            280,1230, N, "Slick",      1.55);
                Car r34z  = mkCar("Nissan GT-R R34 Z-Tune",     493,1450, T, "Slick",      1.72);
                Car gto   = mkCar("Toyota GT-One TS020",        600,1000, N, "Slick",      1.88);
                Car b787  = mkCar("Mazda 787B",                 700, 925, N, "Slick",      1.85);
                Car hks   = mkCar("HKS Time Attack Evo",        600,1320, T, "Slick",      1.80);
                Car nismo = mkCar("Nismo 400R",                 400,1430, T, "Slick",      1.68);

                List<Car> ac = carRepo.saveAll(List.of(
                    ae86,mx5,crx,ek9,star,
                    s13,s14,fc3s,evo4,alt,nsx,
                    r34,fd3s,supra,evo9,sti5,nsxr,
                    r34z,gto,b787,hks,nismo
                ));

                // ── Upgrades des voitures adversaires ────────────
                // Palier 1 (idx 0-4)   : stock — pas d'upgrade
                // Palier 2 (idx 5-10)  : moteur Sport, suspension Sport
                // Palier 3 (idx 11-16) : moteur Racing, suspension Racing, pneus Sport
                // Palier 4 (idx 17-21) : tout Racing
                for (int i = 0; i < ac.size(); i++) {
                    CarUpgrade upg = new CarUpgrade();
                    upg.setCar(ac.get(i));
                    if (i >= 5 && i <= 10) {
                        // Palier 2 : Sport
                        upg.setEngineLevel(1);
                        upg.setSuspensionLevel(1);
                        // Applique les effets directement sur la voiture
                        Car c = ac.get(i);
                        c.setPower((int)(c.getPower() * 1.13));
                        c.setGripModifier(c.getGripModifier() + 0.06);
                        carRepo.save(c);
                    } else if (i >= 11 && i <= 16) {
                        // Palier 3 : Racing moteur + suspension
                        upg.setEngineLevel(2);
                        upg.setSuspensionLevel(2);
                        upg.setTiresLevel(1);
                        Car c = ac.get(i);
                        c.setPower((int)(c.getPower() * 1.25));
                        c.setGripModifier(c.getGripModifier() + 0.14);
                        carRepo.save(c);
                    } else if (i >= 17) {
                        // Palier 4 : tout Racing
                        upg.setEngineLevel(2); upg.setTransmissionLevel(2);
                        upg.setSuspensionLevel(2); upg.setBrakesLevel(2);
                        upg.setWeightLevel(2); upg.setTiresLevel(2);
                        Car c = ac.get(i);
                        c.setPower((int)(c.getPower() * 1.25));
                        c.setWeight((int)(c.getWeight() * 0.90));
                        c.setGripModifier(c.getGripModifier() + 0.20);
                        carRepo.save(c);
                    }
                    upgradeRepo.save(upg);
                }

                // ══ GANG : Night Kids (Parking 1 Akihabara) ══
                Racers r_nk1 = mkGangMember("Night Kids","Itsuki Takeuchi",   ac.get(1));
                Racers r_nk2 = mkGangMember("Night Kids","Koichiro Iketani",  ac.get(2));
                Racers r_nk3 = mkGangMember("Night Kids","Kenji",             ac.get(3));
                Racers r_nk4 = mkGangMember("Night Kids","Fumio Suetake",     ac.get(4));
                Racers r_nk5 = mkGangMember("Night Kids","Atsushi Ogata",     ac.get(0));
                Racers r_nk6 = mkGangMember("Night Kids","Ryuji Ikeda",       ac.get(1));
                Racers boss_nk = mkBoss("Night Kids","Takumi Fujiwara",       ac.get(0));
                // Random sur parking 1
                Racers r1_rand1 = mkRacer("Satoshi",  ac.get(2));
                Racers r1_rand2 = mkRacer("Hiroshi",  ac.get(4));

                // ══ GANG : Emperor (Parking 2 Shibuya) ══
                Racers r_em1 = mkGangMember("Emperor","Sudo Kazuhiro",        ac.get(5));
                Racers r_em2 = mkGangMember("Emperor","Seiji Iwaki",          ac.get(8));
                Racers r_em3 = mkGangMember("Emperor","Naoto Irizaki",        ac.get(6));
                Racers r_em4 = mkGangMember("Emperor","Shingo Shoji",         ac.get(4));
                Racers r_em5 = mkGangMember("Emperor","Tomo Noda",            ac.get(5));
                Racers r_em6 = mkGangMember("Emperor","Akira Kaga",           ac.get(6));
                Racers boss_em = mkBoss("Emperor","Kyoichi Sudo",             ac.get(8));
                Racers r2_rand1 = mkRacer("Daisuke",  ac.get(7));

                // ══ GANG : Red Suns (Parking 3 Wangan) ══
                Racers r_rs1 = mkGangMember("Red Suns","Fumihiro Koichi",     ac.get(7));
                Racers r_rs2 = mkGangMember("Red Suns","Mako Sato",           ac.get(7));
                Racers r_rs3 = mkGangMember("Red Suns","Nakazato Hiroyuki",   ac.get(5));
                Racers r_rs4 = mkGangMember("Red Suns","Ryo Watanabe",        ac.get(6));
                Racers r_rs5 = mkGangMember("Red Suns","Jun Omori",           ac.get(9));
                Racers r_rs6 = mkGangMember("Red Suns","Wataru Akiyama",      ac.get(10));
                Racers boss_rs = mkBoss("Red Suns","Ryosuke Takahashi",       ac.get(11));
                Racers r3_rand1 = mkRacer("Yutaka",   ac.get(9));
                Racers r3_rand2 = mkRacer("Makoto",   ac.get(10));

                // ══ GANG : Purple Shadow (Parking 4 Bayshore) ══
                Racers r_ps1 = mkGangMember("Purple Shadow","Takeshi Nakazato",ac.get(5));
                Racers r_ps2 = mkGangMember("Purple Shadow","Kenta Nakamura", ac.get(8));
                Racers r_ps3 = mkGangMember("Purple Shadow","Toshiya Joshima",ac.get(9));
                Racers r_ps4 = mkGangMember("Purple Shadow","Hideo Minagawa", ac.get(10));
                Racers r_ps5 = mkGangMember("Purple Shadow","Daiki Ninomiya", ac.get(8));
                Racers r_ps6 = mkGangMember("Purple Shadow","Atsuro Kawai",   ac.get(9));
                Racers boss_ps = mkBoss("Purple Shadow","Keisuke Takahashi",  ac.get(12));
                Racers r4_rand1 = mkRacer("Noboru",   ac.get(10));

                // ══ GANG : Rotary Alliance (Parking 5 C1) ══
                Racers r_ra1 = mkGangMember("Rotary Alliance","Kai Kogashiwa",ac.get(14));
                Racers r_ra2 = mkGangMember("Rotary Alliance","Rin Hojo",     ac.get(12));
                Racers r_ra3 = mkGangMember("Rotary Alliance","Shinji Inui",  ac.get(13));
                Racers r_ra4 = mkGangMember("Rotary Alliance","Kou Takagi",   ac.get(14));
                Racers r_ra5 = mkGangMember("Rotary Alliance","Dori-Kichi",   ac.get(12));
                Racers r_ra6 = mkGangMember("Rotary Alliance","Eiji Kubo",    ac.get(13));
                Racers boss_ra = mkBoss("Rotary Alliance","Bunta Fujiwara",   ac.get(13));
                Racers r5_rand1 = mkRacer("Tetsuya",  ac.get(11));
                Racers r5_rand2 = mkRacer("Yoko",     ac.get(12));

                // ══ GANG : Touge Masters (Parking 6 Touge) ══
                Racers r_tm1 = mkGangMember("Touge Masters","Seiji Iwaki",    ac.get(14));
                Racers r_tm2 = mkGangMember("Touge Masters","Hideo Minagawa", ac.get(15));
                Racers r_tm3 = mkGangMember("Touge Masters","Toshiya Joshima",ac.get(14));
                Racers r_tm4 = mkGangMember("Touge Masters","Kai Kogashiwa",  ac.get(15));
                Racers r_tm5 = mkGangMember("Touge Masters","Shinichi Iketani",ac.get(16));
                Racers r_tm6 = mkGangMember("Touge Masters","Atsuro Kawai",   ac.get(16));
                Racers boss_tm = mkBoss("Touge Masters","Atsuro Kawai (Boss)",ac.get(16));
                Racers r6_rand1 = mkRacer("Goro",     ac.get(15));

                // ══ GANG : GT Legends (Parking 7 Tsukuba) ══
                Racers r_gl1 = mkGangMember("GT Legends","Hideo Minagawa",    ac.get(17));
                Racers r_gl2 = mkGangMember("GT Legends","Nobuhiko Akagi",    ac.get(18));
                Racers r_gl3 = mkGangMember("GT Legends","Kozo Hoshino",      ac.get(19));
                Racers r_gl4 = mkGangMember("GT Legends","Takayuki Kuroki",   ac.get(17));
                Racers r_gl5 = mkGangMember("GT Legends","Ryu Watanabe",      ac.get(18));
                Racers r_gl6 = mkGangMember("GT Legends","Tatsuya Shiono",    ac.get(19));
                Racers boss_gl = mkBoss("GT Legends","Re Amemiya",            ac.get(19));
                Racers r7_rand1 = mkRacer("Anonymous", ac.get(17));

                // ══ GANG : Zero Gravity (Parking 8 Fuji) ══
                Racers r_zg1 = mkGangMember("Zero Gravity","Re Amemiya",      ac.get(19));
                Racers r_zg2 = mkGangMember("Zero Gravity","Nobuhiko Akagi",  ac.get(20));
                Racers r_zg3 = mkGangMember("Zero Gravity","Kozo Hoshino",    ac.get(21));
                Racers r_zg4 = mkGangMember("Zero Gravity","Takayuki Kuroki", ac.get(19));
                Racers r_zg5 = mkGangMember("Zero Gravity","Hideo Minagawa",  ac.get(20));
                Racers r_zg6 = mkGangMember("Zero Gravity","Tatsuya Shiono",  ac.get(21));
                Racers boss_zg = mkBoss("Zero Gravity","Kozo Hoshino",        ac.get(21));
                Racers r8_rand1 = mkRacer("The Ghost",  ac.get(20));

                List<Racers> ar = racersRepo.saveAll(List.of(
                    // Night Kids
                    r_nk1,r_nk2,r_nk3,r_nk4,r_nk5,r_nk6,boss_nk,r1_rand1,r1_rand2,
                    // Emperor
                    r_em1,r_em2,r_em3,r_em4,r_em5,r_em6,boss_em,r2_rand1,
                    // Red Suns
                    r_rs1,r_rs2,r_rs3,r_rs4,r_rs5,r_rs6,boss_rs,r3_rand1,r3_rand2,
                    // Purple Shadow
                    r_ps1,r_ps2,r_ps3,r_ps4,r_ps5,r_ps6,boss_ps,r4_rand1,
                    // Rotary Alliance
                    r_ra1,r_ra2,r_ra3,r_ra4,r_ra5,r_ra6,boss_ra,r5_rand1,r5_rand2,
                    // Touge Masters
                    r_tm1,r_tm2,r_tm3,r_tm4,r_tm5,r_tm6,boss_tm,r6_rand1,
                    // GT Legends
                    r_gl1,r_gl2,r_gl3,r_gl4,r_gl5,r_gl6,boss_gl,r7_rand1,
                    // Zero Gravity
                    r_zg1,r_zg2,r_zg3,r_zg4,r_zg5,r_zg6,boss_zg,r8_rand1
                ));

                // Helper to slice ar list

                // P1: Akihabara Backstreet — Night Kids (9 racers: 6 members + boss + 2 randoms)
                Parking p1 = new Parking(); p1.setName("Akihabara Backstreet");
                p1.setRace(new ArrayList<>(List.of(rAkihabara,rTsukuba)));
                p1.setRacers(new ArrayList<>(List.of(ar.get(0),ar.get(1),ar.get(2),ar.get(3),ar.get(4),ar.get(5),ar.get(6),ar.get(7),ar.get(8))));
                parkingRepo.save(p1);

                // P2: Shibuya Night Run — Emperor (8 racers)
                Parking p2 = new Parking(); p2.setName("Shibuya Night Run");
                p2.setRace(new ArrayList<>(List.of(rShuto,rTsukuba)));
                p2.setRacers(new ArrayList<>(List.of(ar.get(9),ar.get(10),ar.get(11),ar.get(12),ar.get(13),ar.get(14),ar.get(15),ar.get(16))));
                parkingRepo.save(p2);

                // P3: Wangan Night — Red Suns (9 racers)
                Parking p3 = new Parking(); p3.setName("Wangan Night");
                p3.setRace(new ArrayList<>(List.of(rWangan,rShuto)));
                p3.setRacers(new ArrayList<>(List.of(ar.get(17),ar.get(18),ar.get(19),ar.get(20),ar.get(21),ar.get(22),ar.get(23),ar.get(24),ar.get(25))));
                parkingRepo.save(p3);

                // P4: Bayshore Circuit — Purple Shadow (8 racers)
                Parking p4 = new Parking(); p4.setName("Bayshore Circuit");
                p4.setRace(new ArrayList<>(List.of(rBayshore,rYokohama)));
                p4.setRacers(new ArrayList<>(List.of(ar.get(26),ar.get(27),ar.get(28),ar.get(29),ar.get(30),ar.get(31),ar.get(32),ar.get(33))));
                parkingRepo.save(p4);

                // P5: C1 Underground — Rotary Alliance (9 racers)
                Parking p5 = new Parking(); p5.setName("C1 Underground");
                p5.setRace(new ArrayList<>(List.of(rC1,rAkihabara)));
                p5.setRacers(new ArrayList<>(List.of(ar.get(34),ar.get(35),ar.get(36),ar.get(37),ar.get(38),ar.get(39),ar.get(40),ar.get(41),ar.get(42))));
                parkingRepo.save(p5);

                // P6: Touge Highland — Touge Masters (8 racers)
                Parking p6 = new Parking(); p6.setName("Touge Highland");
                p6.setRace(new ArrayList<>(List.of(rHakone,rBayshore)));
                p6.setRacers(new ArrayList<>(List.of(ar.get(43),ar.get(44),ar.get(45),ar.get(46),ar.get(47),ar.get(48),ar.get(49),ar.get(50))));
                parkingRepo.save(p6);

                // P7: Tsukuba Challenge — GT Legends (8 racers)
                Parking p7 = new Parking(); p7.setName("Tsukuba Challenge");
                p7.setRace(new ArrayList<>(List.of(rTsukuba,rNikko)));
                p7.setRacers(new ArrayList<>(List.of(ar.get(51),ar.get(52),ar.get(53),ar.get(54),ar.get(55),ar.get(56),ar.get(57),ar.get(58))));
                parkingRepo.save(p7);

                // P8: Fuji Legend Series — Zero Gravity (8 racers)
                Parking p8 = new Parking(); p8.setName("Fuji Legend Series");
                p8.setRace(new ArrayList<>(List.of(rFuji,rWangan)));
                p8.setRacers(new ArrayList<>(List.of(ar.get(59),ar.get(60),ar.get(61),ar.get(62),ar.get(63),ar.get(64),ar.get(65),ar.get(66))));
                parkingRepo.save(p8);

                // ══════════════════════════════════════════════════
                //  WANDERERS — Adversaires spéciaux débloqués par réputation
                //  Inspirés des Devil Z, Blackbird, etc. de Wangan Midnight
                //  et des wanderers de Tokyo Xtreme Racer.
                //
                //  Palier 1 — Rep  50 : pilotes mystères débutants
                //  Palier 2 — Rep 150 : spécialistes de niche
                //  Palier 3 — Rep 350 : legends urbaines
                //  Palier 4 — Rep 700 : fantômes de la route
                //
                //  Chaque wanderer possède une voiture modifiée unique
                //  qui se met en vente chez Legend Motorsport si on le bat.
                // ══════════════════════════════════════════════════

                // ── Voitures spéciales des wanderers (en vente si battu) ──
                // Palier 1 — machines préparées accessibles
                Car w_sp1 = mkSpecialCar("AE86 Trueno [Fujiwara Tofu]",    178,  880, N, "Sport",      1.55, 38000);
                Car w_sp2 = mkSpecialCar("MX-5 NA [Roadster Spec-R]",       142,  910, N, "Semi-Slick", 1.52, 32000);
                Car w_sp3 = mkSpecialCar("Civic EK9 [Street Fighter]",       215, 1020, N, "Sport",      1.40, 42000);
                Car w_sp4 = mkSpecialCar("Starlet GT [Touge King]",          165,  890, T, "Semi-Slick", 1.38, 35000);
                Car w_sp5 = mkSpecialCar("S13 Silvia [Midnight Drifter]",    240, 1150, T, "Semi-Slick", 1.32, 55000);

                // Palier 2 — machines sérieusement préparées
                Car w_sp6 = mkSpecialCar("RX-7 FC [Rotary Devil]",          255, 1180, T, "Semi-Slick", 1.42, 82000);
                Car w_sp7 = mkSpecialCar("Altezza [Phantom Sedan]",          260, 1180, N, "Semi-Slick", 1.38, 75000);
                Car w_sp8 = mkSpecialCar("NSX [Silent Hunter]",              310, 1220, N, "Slick",      1.52, 145000);
                Car w_sp9 = mkSpecialCar("Evo IV [Emperor RS]",              320, 1210, T, "Semi-Slick", 1.52, 110000);
                Car w_sp10= mkSpecialCar("S15 Silvia [Wangan Ghost]",        290, 1180, T, "Semi-Slick", 1.35, 95000);

                // Palier 3 — légendes urbaines
                Car w_sp11= mkSpecialCar("RX-7 FD [Devil Z Rotary]",        340, 1190, T, "Slick",      1.58, 195000);
                Car w_sp12= mkSpecialCar("Supra A80 [Last King]",            360, 1430, T, "Slick",      1.45, 185000);
                Car w_sp13= mkSpecialCar("GT-R R32 [Godzilla Spec]",         380, 1380, T, "Slick",      1.58, 220000);
                Car w_sp14= mkSpecialCar("NSX-R [White Ghost]",              340, 1180, N, "Slick",      1.65, 210000);
                Car w_sp15= mkSpecialCar("Evo VI [Makinen Evo Spec]",        360, 1290, T, "Slick",      1.62, 200000);

                // Palier 4 — fantômes de la route (quasi-imbattables)
                Car w_sp16= mkSpecialCar("R34 GT-R [Black Reaper]",          480, 1420, T, "Slick",      1.72, 420000);
                Car w_sp17= mkSpecialCar("787B [Rotary God]",                650,  960, N, "Slick",      1.82, 580000);
                Car w_sp18= mkSpecialCar("GT-One [Phantom TS020]",           560, 1020, N, "Slick",      1.82, 520000);
                Car w_sp19= mkSpecialCar("HKS Evo [Time God]",               580, 1290, T, "Slick",      1.78, 540000);
                Car w_sp20= mkSpecialCar("Z-Tune [Absolute Zero]",           520, 1410, T, "Slick",      1.76, 480000);

                // Sauvegarde des voitures spéciales
                carRepo.saveAll(List.of(
                    w_sp1,w_sp2,w_sp3,w_sp4,w_sp5,
                    w_sp6,w_sp7,w_sp8,w_sp9,w_sp10,
                    w_sp11,w_sp12,w_sp13,w_sp14,w_sp15,
                    w_sp16,w_sp17,w_sp18,w_sp19,w_sp20
                ));

                // ── Voitures de combat des wanderers ──────────────────
                // (différentes des voitures spéciales à débloquer)
                Car wc1  = mkCar("AE86 Trueno Prep",    158,  890, N, "Sport",      1.48);
                Car wc2  = mkCar("MX-5 NA Prep",        128,  920, N, "Semi-Slick", 1.48);
                Car wc3  = mkCar("Civic EK9 Prep",      195, 1030, N, "Sport",      1.36);
                Car wc4  = mkCar("Starlet GT Prep",     148,  900, T, "Semi-Slick", 1.34);
                Car wc5  = mkCar("S13 Silvia Prep",     220, 1160, T, "Semi-Slick", 1.28);
                Car wc6  = mkCar("RX-7 FC Prep",        235, 1195, T, "Semi-Slick", 1.38);
                Car wc7  = mkCar("Altezza Prep",        240, 1190, N, "Semi-Slick", 1.34);
                Car wc8  = mkCar("NSX Prep",            285, 1230, N, "Slick",      1.48);
                Car wc9  = mkCar("Evo IV Prep",         300, 1220, T, "Semi-Slick", 1.48);
                Car wc10 = mkCar("S15 Silvia Prep",     265, 1190, T, "Semi-Slick", 1.30);
                Car wc11 = mkCar("RX-7 FD Prep",       310, 1200, T, "Slick",      1.52);
                Car wc12 = mkCar("Supra A80 Prep",     340, 1440, T, "Slick",      1.40);
                Car wc13 = mkCar("GT-R R32 Prep",      355, 1390, T, "Slick",      1.52);
                Car wc14 = mkCar("NSX-R Prep",         315, 1190, N, "Slick",      1.60);
                Car wc15 = mkCar("Evo VI Prep",        335, 1300, T, "Slick",      1.55);
                Car wc16 = mkCar("R34 GT-R Prep",      450, 1430, T, "Slick",      1.68);
                Car wc17 = mkCar("787B Prep",          620,  970, N, "Slick",      1.78);
                Car wc18 = mkCar("GT-One Prep",        530, 1030, N, "Slick",      1.78);
                Car wc19 = mkCar("HKS Evo Prep",       550, 1300, T, "Slick",      1.74);
                Car wc20 = mkCar("Z-Tune Prep",        490, 1420, T, "Slick",      1.72);

                List<Car> wandererCars = carRepo.saveAll(List.of(
                    wc1,wc2,wc3,wc4,wc5,wc6,wc7,wc8,wc9,wc10,
                    wc11,wc12,wc13,wc14,wc15,wc16,wc17,wc18,wc19,wc20
                ));

                // ── Création des wanderers ─────────────────────────────
                // Palier 1 — Rep 50
                Racers w1  = mkSpecial("The Drifter",       wandererCars.get(0),   50,  w_sp1);
                Racers w2  = mkSpecial("Shadow Driver",     wandererCars.get(1),   50,  w_sp2);
                Racers w3  = mkSpecial("Street Fighter",    wandererCars.get(2),   75,  w_sp3);
                Racers w4  = mkSpecial("Touge King",        wandererCars.get(3),   75,  w_sp4);
                Racers w5  = mkSpecial("Midnight Drifter",  wandererCars.get(4),  100,  w_sp5);
                // Palier 2 — Rep 150
                Racers w6  = mkSpecial("Rotary Devil",      wandererCars.get(5),  150,  w_sp6);
                Racers w7  = mkSpecial("Phantom Sedan",     wandererCars.get(6),  175,  w_sp7);
                Racers w8  = mkSpecial("Silent Hunter",     wandererCars.get(7),  200,  w_sp8);
                Racers w9  = mkSpecial("Emperor RS",        wandererCars.get(8),  225,  w_sp9);
                Racers w10 = mkSpecial("Wangan Ghost",      wandererCars.get(9),  250,  w_sp10);
                // Palier 3 — Rep 350
                Racers w11 = mkSpecial("Devil Z",           wandererCars.get(10), 350,  w_sp11);
                Racers w12 = mkSpecial("Last King",         wandererCars.get(11), 400,  w_sp12);
                Racers w13 = mkSpecial("Godzilla",          wandererCars.get(12), 450,  w_sp13);
                Racers w14 = mkSpecial("White Ghost",       wandererCars.get(13), 500,  w_sp14);
                Racers w15 = mkSpecial("Makinen Legend",    wandererCars.get(14), 550,  w_sp15);
                // Palier 4 — Rep 700
                Racers w16 = mkSpecial("Black Reaper",      wandererCars.get(15), 700,  w_sp16);
                Racers w17 = mkSpecial("Rotary God",        wandererCars.get(16), 750,  w_sp17);
                Racers w18 = mkSpecial("Phantom TS020",     wandererCars.get(17), 800,  w_sp18);
                Racers w19 = mkSpecial("Time God",          wandererCars.get(18), 850,  w_sp19);
                Racers w20 = mkSpecial("Absolute Zero",     wandererCars.get(19), 1000, w_sp20);

                List<Racers> wanderers = racersRepo.saveAll(List.of(
                    w1,w2,w3,w4,w5,w6,w7,w8,w9,w10,
                    w11,w12,w13,w14,w15,w16,w17,w18,w19,w20
                ));

                // ── Répartition des wanderers sur les parkings ────────
                // Chaque parking reçoit les wanderers de son niveau
                // + les wanderers des paliers supérieurs (si rep atteinte)
                // Palier 1 → P1, P2   |   Palier 2 → P3, P4
                // Palier 3 → P5, P6   |   Palier 4 → P7, P8

                // Ajoute wanderers P1 et P2 (palier 1)
                p1.getRacers().addAll(List.of(wanderers.get(0), wanderers.get(1)));
                p2.getRacers().addAll(List.of(wanderers.get(2), wanderers.get(3), wanderers.get(4)));
                parkingRepo.save(p1);
                parkingRepo.save(p2);

                // Ajoute wanderers P3 et P4 (palier 2)
                p3.getRacers().addAll(List.of(wanderers.get(5), wanderers.get(6)));
                p4.getRacers().addAll(List.of(wanderers.get(7), wanderers.get(8), wanderers.get(9)));
                parkingRepo.save(p3);
                parkingRepo.save(p4);

                // Ajoute wanderers P5 et P6 (palier 3)
                p5.getRacers().addAll(List.of(wanderers.get(10), wanderers.get(11)));
                p6.getRacers().addAll(List.of(wanderers.get(12), wanderers.get(13), wanderers.get(14)));
                parkingRepo.save(p5);
                parkingRepo.save(p6);

                // Ajoute wanderers P7 et P8 (palier 4)
                p7.getRacers().addAll(List.of(wanderers.get(15), wanderers.get(16)));
                p8.getRacers().addAll(List.of(wanderers.get(17), wanderers.get(18), wanderers.get(19)));
                parkingRepo.save(p7);
                parkingRepo.save(p8);

                System.out.println("[INIT] 20 wanderers crees et distribues sur les parkings.");

                System.out.println("[INIT] 8 parkings + 22 adversaires + 20 wanderers crees.");
            }

            // 5. ÉCURIES SRC
            if (srcTeamRepo.count() == 0) {
                // Rang 1 = meilleure écurie, rang 10 = backmarker
                // Rep requise globale : 300. Chaque écurie a son propre seuil d'invitation.
                mkTeam(srcTeamRepo,"Nismo Factory",       1, 300,"Nissan GT-R R34 JGTC",  520,1050,1.78,"TURBO", "#ff003c",2500000,"L'écurie officielle Nissan. Imbattable.");
                mkTeam(srcTeamRepo,"TOM'S Toyota",        2, 300,"Toyota Supra GT500",     495,1080,1.74,"TURBO", "#ff8c00",2200000,"Domination sur circuit fermé depuis 10 ans.");
                mkTeam(srcTeamRepo,"Castrol Honda",       3, 320,"Honda NSX GT500",        470,1040,1.80,"NATURAL","#ffffff",2000000,"Équilibre parfait : légèreté et agilité.");
                mkTeam(srcTeamRepo,"RE Amemiya Mazda",    4, 340,"Mazda RX-7 JGTC",       450,1010,1.76,"TURBO", "#ff6600",1800000,"Le rotary en compétition. Sonorité unique.");
                mkTeam(srcTeamRepo,"Ralliart Mitsubishi", 5, 360,"Mitsubishi Evo JGTC",   435,1150,1.70,"TURBO", "#cc0000",1600000,"La fureur de la piste, héritière du WRC.");
                mkTeam(srcTeamRepo,"STi Subaru",          6, 380,"Subaru Impreza JGTC",   420,1160,1.68,"TURBO", "#0055ff",1400000,"AWD sur circuit fermé — grip légendaire.");
                mkTeam(srcTeamRepo,"HKS Racing",          7, 400,"HKS GT-R Spec",         410,1120,1.65,"TURBO", "#00cc44",1200000,"Tuner indépendant. Préparation extrême.");
                mkTeam(srcTeamRepo,"Top Secret Racing",   8, 450,"Top Secret Supra",      390,1200,1.60,"TURBO", "#9900ff",1000000,"Vitesse pure, setup agressif.");
                mkTeam(srcTeamRepo,"Garage Saurus",       9, 500,"GS Silvia S15 JGTC",    365,1120,1.55,"TURBO", "#00cccc", 800000,"Petite écurie, grands espoirs.");
                mkTeam(srcTeamRepo,"JGTC Privateers",    10, 300,"Privateer AE86 Spec",   310, 920,1.68,"NATURAL","#888888", 500000,"Les guerriers de la piste. Accessibles à tous.");
                System.out.println("[INIT] 10 écuries SRC créées.");
            }
        };
    }

    private void mkTeam(SrcTeamRepository repo, String name, int rank, int repReq,
                        String carName, int pw, int wt, double grip,
                        String asp, String color, long budget, String desc) {
        SrcTeam t = new SrcTeam();
        t.setName(name); t.setRank(rank); t.setReputationRequired(repReq);
        t.setCarName(carName); t.setCarPower(pw); t.setCarWeight(wt);
        t.setCarGrip(grip); t.setCarAspiration(asp); t.setTeamColor(color);
        t.setBudget(budget); t.setDescription(desc);
        t.setEngineBonus(0); t.setGripBonus(0); t.setSeasonEarnings(0);
        repo.save(t);
    }

    private Dealership newDealer(String name) {
        Dealership d = new Dealership();
        d.setName(name);
        d.setCars(new ArrayList<>());
        return d;
    }

    /**
     * Crée un circuit avec segments intercalés droite/virage.
     * @param name    nom du circuit
     * @param segs    segments ex: "S:400,C:90,S:300,C:60,S:500,C:45"
     *                S = droite (longueur fictive), C = virage (angle 30-150°)
     */
    private Race mkRace(String name, String segs) {
        Race r = new Race();
        r.setName(name);
        r.setSegments(segs);
        // Calcule les % droite/virage pour rétrocompat affichage
        int totalS = 0, countS = 0, totalC = 0, countC = 0;
        for (String seg : segs.split(",")) {
            String[] parts = seg.trim().split(":");
            if ("S".equals(parts[0])) { totalS += Integer.parseInt(parts[1]); countS++; }
            else if ("C".equals(parts[0])) { totalC += Integer.parseInt(parts[1]); countC++; }
        }
        int total = totalS + totalC;
        r.setStraigthLine(total > 0 ? Math.round(totalS * 100f / total) : 50);
        r.setCorner(total > 0 ? Math.round(totalC * 100f / total) : 50);
        return r;
    }

    private Car mkCar(String name, int pw, int wt, AspirationType asp, String tire, double grip) {
        Car c = new Car(); c.setName(name); c.setPower(pw); c.setWeight(wt);
        c.setAspiration(asp); c.setTireType(tire); c.setGripModifier(grip);
        c.setPrice(0); c.setGarage(null); c.setDealership(null); return c;
    }

    private void lc(Dealership d, String name, int pw, int wt, long price,
                    AspirationType asp, String tire, double grip) {
        Car c = new Car(); c.setName(name); c.setPower(pw); c.setWeight(wt);
        c.setPrice(price); c.setAspiration(asp); c.setTireType(tire);
        c.setGripModifier(grip); c.setGarage(null); c.setDealership(d);
        d.getCars().add(c);
    }

    private Racers mkRacer(String name, Car car) {
        Racers r = new Racers(); r.setName(name); r.setCar(car);
        r.setPrefix(""); r.setGangMember(false); r.setBoss(false);
        r.setGangName(""); r.setSpecial(false); r.setReputationRequired(0);
        return r;
    }

    /** Membre de gang */
    private Racers mkGangMember(String gangName, String memberName, Car car) {
        Racers r = new Racers();
        r.setName(memberName);
        r.setCar(car);
        r.setPrefix("[" + gangName + "]");
        r.setGangMember(true);
        r.setBoss(false);
        r.setGangName(gangName);
        r.setSpecial(false);
        r.setReputationRequired(0);
        return r;
    }

    /** Boss de gang */
    private Racers mkBoss(String gangName, String bossName, Car car) {
        Racers r = new Racers();
        r.setName(bossName);
        r.setCar(car);
        r.setPrefix("[Boss]");
        r.setGangMember(true);
        r.setBoss(true);
        r.setGangName(gangName);
        r.setSpecial(false);
        r.setReputationRequired(0);
        return r;
    }

    /** Adversaire spécial (déblocable par réputation) */
    /** Voiture spéciale mise en vente si le wanderer est battu */
    private Car mkSpecialCar(String name, int pw, int wt,
                              AspirationType asp, String tire, double grip, long price) {
        Car c = new Car();
        c.setName(name); c.setPower(pw); c.setWeight(wt);
        c.setAspiration(asp); c.setTireType(tire); c.setGripModifier(grip);
        c.setPrice(price); c.setGarage(null); c.setDealership(null);
        return c;
    }

    private Racers mkSpecial(String name, Car car, int repRequired, Car specialCarForSale) {
        Racers r = new Racers();
        r.setName(name);
        r.setCar(car);
        r.setPrefix("[SPECIAL]");
        r.setGangMember(false);
        r.setBoss(false);
        r.setGangName("");
        r.setSpecial(true);
        r.setReputationRequired(repRequired);
        r.setSpecialCarForSale(specialCarForSale);
        return r;
    }
}
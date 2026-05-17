package com.example.bytefinder.data

/**
 * MockDataProvider — Fornece dados mock robustos para demonstração offline.
 *
 * Contém restaurantes, pratos, categorias, avaliações e localizações reais
 * de Lisboa, Porto e Coimbra com relações lógicas (IDs, categorias, ratings).
 *
 * Utilizado pelo DataRepository como fallback quando a API não está disponível.
 */
object MockDataProvider {

    // ─── Categorias ─────────────────────────────────────────────────────
    val categorias = listOf(
        "Pratos Tradicionais", "Pizza", "Marisco", "Francesinha", "Hambúrguer",
        "Sushi", "Pasta", "Sobremesas", "Bifana", "Bacalhau"
    )

    // ─── Imagens por Categoria (URLs Wikipedia/Unsplash) ────────────────
    val categoryImages = mapOf(
        "Todos" to "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&q=80&w=200",
        "Pizza" to "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg/330px-Eq_it-na_pizza-margherita_sep2005_sml.jpg",
        "Marisco" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Arroz_de_marisco_in_Lisbon.jpg/330px-Arroz_de_marisco_in_Lisbon.jpg",
        "Francesinha" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Francesinha_Sandwich_%28cropped%29.jpg/330px-Francesinha_Sandwich_%28cropped%29.jpg",
        "Hambúrguer" to "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&q=80&w=200",
        "Sushi" to "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?auto=format&fit=crop&q=80&w=200",
        "Pasta" to "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Espaguetis_carbonara.jpg/330px-Espaguetis_carbonara.jpg",
        "Sobremesas" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Pastel_de_nata_%2818616473070%29.jpg/330px-Pastel_de_nata_%2818616473070%29.jpg",
        "Bifana" to "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Bifana_on_a_plate.jpg/330px-Bifana_on_a_plate.jpg",
        "Pratos Tradicionais" to "https://commons.wikimedia.org/wiki/Special:Redirect/file/Cozido%20a%20portuguesa%201.JPG",
        "Bacalhau" to "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Bacalhau_a_Bras.jpg/330px-Bacalhau_a_Bras.jpg"
    )

    fun getCategoryImageUrl(name: String): String =
        categoryImages[name]
            ?: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&q=80&w=200"

    // ─── Dados dos Restaurantes ─────────────────────────────────────────

    data class MockRestaurante(
        val id: Int,
        val nome: String,
        val morada: String,
        val cidade: String,
        val zona: String,
        val latitude: Double,
        val longitude: Double
    )

    val restaurantes = listOf(
        MockRestaurante(2, "Pizzaria Luzzo Amadora", "Av. Cruzeiro Seixas 5 e 7", "Amadora", "Centro", 38.7763, -9.2197),
        MockRestaurante(3, "Cervejaria Ramiro", "Av. Almirante Reis 1H", "Lisboa", "Intendente", 38.7223, -9.1353),
        MockRestaurante(5, "Café Santiago", "Rua de Passos Manuel 226", "Porto", "Bolhão", 41.1496, -8.6100),
        MockRestaurante(7, "Pastéis de Belém", "Rua de Belém 84-92", "Lisboa", "Belém", 38.6975, -9.2030),
        MockRestaurante(9, "Hamburgueria do Bairro", "Rua da Madalena 200", "Lisboa", "Baixa", 38.7100, -9.1330),
        MockRestaurante(10, "Sushicafé Avenida", "Rua Barata Salgueiro 28", "Lisboa", "Avenida", 38.7260, -9.1470),
        MockRestaurante(11, "Pasta Non Basta", "Rua do Alecrim 21", "Lisboa", "Chiado", 38.7100, -9.1450),
        // Novos restaurantes para comparação por prato
        MockRestaurante(12, "Bufete Fase", "Rua da Fábrica 32", "Porto", "Bolhão", 41.1490, -8.6090),
        MockRestaurante(13, "Cervejaria Gazela", "Travessa Cimo de Vila 4", "Porto", "Bolhão", 41.1488, -8.6095),
        MockRestaurante(14, "A Cozinha do Manel", "Rua Augusta 45", "Lisboa", "Baixa", 38.7110, -9.1390),
        MockRestaurante(15, "O Velho Eurico", "Largo de São Cristóvão 3", "Lisboa", "Alfama", 38.7140, -9.1310),
        MockRestaurante(16, "Pizzeria Romana al Taglio", "Rua Garrett 56", "Lisboa", "Chiado", 38.7108, -9.1425),
        MockRestaurante(17, "Pizza a Pezzi", "Rua de Santa Catarina 112", "Porto", "Bolhão", 41.1510, -8.6088),
        MockRestaurante(18, "Sea Me", "Rua do Loreto 21", "Lisboa", "Chiado", 38.7115, -9.1445),
        MockRestaurante(19, "Marisqueira do Lis", "Av. da Liberdade 155", "Lisboa", "Avenida", 38.7195, -9.1462),
        MockRestaurante(20, "Tasca do Chico", "Rua do Diário de Notícias 39", "Lisboa", "Chiado", 38.7118, -9.1440),
        MockRestaurante(21, "Confraria do Sushi", "Rua Castilho 77", "Lisboa", "Avenida", 38.7270, -9.1500),
        MockRestaurante(22, "Trattoria Amadora", "Rua da Misericórdia 14", "Lisboa", "Chiado", 38.7112, -9.1438),
        MockRestaurante(23, "Nata Lisboa", "Rua da Prata 78", "Lisboa", "Baixa", 38.7107, -9.1368),
        // Novos restaurantes — Bifana
        MockRestaurante(24, "Casa das Bifanas", "Rua dos Correeiros 89", "Lisboa", "Baixa", 38.7115, -9.1380),
        MockRestaurante(25, "O Trevo", "Rua de Santa Catarina 200", "Porto", "Bolhão", 41.1520, -8.6070),
        MockRestaurante(26, "Conga", "Rua do Bonjardim 312", "Porto", "Bolhão", 41.1505, -8.6098),
        // Novos restaurantes — Bacalhau
        MockRestaurante(27, "Solar dos Presuntos", "Rua das Portas de Santo Antão 150", "Lisboa", "Baixa", 38.7175, -9.1410),
        MockRestaurante(28, "Laurentina", "Av. Conde de Valbom 71A", "Lisboa", "Avenida", 38.7330, -9.1500),
        MockRestaurante(29, "O Gaveto", "Rua Roberto Ivens 826", "Porto", "Foz", 41.1550, -8.6780),
        // Novos restaurantes — ronda 2
        MockRestaurante(30, "Zero Zero", "Rua da Escola Politécnica 32", "Lisboa", "Príncipe Real", 38.7168, -9.1490),
        MockRestaurante(31, "Marisqueira Nunes", "Rua Bartolomeu Dias 112", "Lisboa", "Belém", 38.6960, -9.2060),
        MockRestaurante(32, "Lado B", "Rua do Passeio Alegre 564", "Porto", "Foz", 41.1540, -8.6750),
        MockRestaurante(33, "The Good Burger", "Rua de São Paulo 67", "Lisboa", "Cais do Sodré", 38.7065, -9.1450),
        MockRestaurante(34, "Kanazawa", "Rua da Rosa 217", "Lisboa", "Bairro Alto", 38.7130, -9.1450),
        MockRestaurante(35, "Paparico", "Rua de Costa Cabral 2343", "Porto", "Paranhos", 41.1680, -8.6030),
        MockRestaurante(36, "Manteigaria", "Rua do Loreto 2", "Lisboa", "Chiado", 38.7110, -9.1443),
        MockRestaurante(37, "As Bifanas do Afonso", "Praça do Comércio 1", "Lisboa", "Terreiro do Paço", 38.7075, -9.1365),
        MockRestaurante(38, "Adega São Nicolau", "Rua de São Nicolau 1", "Porto", "Ribeira", 41.1405, -8.6140),
        // Novos restaurantes — ronda 3 (mais cobertura por cidade)
        MockRestaurante(39, "Forno d'Oro", "Rua dos Fanqueiros 70", "Lisboa", "Baixa", 38.7105, -9.1365),
        MockRestaurante(40, "Pizzeria Porto Belo", "Rua das Flores 188", "Porto", "Ribeira", 41.1450, -8.6155),
        MockRestaurante(41, "Marisqueira do Douro", "Cais da Ribeira 42", "Porto", "Ribeira", 41.1408, -8.6135),
        MockRestaurante(42, "Burger Lovers Porto", "Rua de Cedofeita 256", "Porto", "Cedofeita", 41.1530, -8.6150),
        MockRestaurante(43, "Sushi Sato", "Rua do Heroísmo 330", "Porto", "Campanhã", 41.1480, -8.5950),
        MockRestaurante(44, "Trattoria Porto", "Rua de Miguel Bombarda 285", "Porto", "Cedofeita", 41.1520, -8.6200),
        MockRestaurante(45, "Nata Pura", "Rua de Santa Catarina 4", "Porto", "Bolhão", 41.1500, -8.6080),
        MockRestaurante(46, "O Bacalhoeiro", "Rua de São Mamede 18", "Lisboa", "Alfama", 38.7135, -9.1305)
    )

    // ─── Pratos Mock ────────────────────────────────────────────────────

    private val _pratos: MutableList<PratoDto> = mutableListOf(
        // ─── Pizza ──────────────────────────────────────────────────────

        // Pizzaria Luzzo Amadora
        PratoDto(1, "Margherita", "Pizza", "Pizza clássica com molho de tomate, mozzarella e manjericão", 13.20, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg/500px-Eq_it-na_pizza-margherita_sep2005_sml.jpg", 2, "Pizzaria Luzzo Amadora", 4.8, 312),
        PratoDto(2, "Pepperoni", "Pizza", "Pizza com pepperoni picante e mozzarella", 15.30, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Pepperoni_pizza.jpg/500px-Pepperoni_pizza.jpg", 2, "Pizzaria Luzzo Amadora", 4.7, 256),
        PratoDto(3, "Funghi", "Pizza", "Pizza com cogumelos frescos e mozzarella", 15.30, "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Pizza_Margherita_stu_spivack.jpg/500px-Pizza_Margherita_stu_spivack.jpg", 2, "Pizzaria Luzzo Amadora", 4.5, 178),

        // Pizzeria Romana al Taglio (Lisboa)
        PratoDto(26, "Margherita al Taglio", "Pizza", "Pizza romana ao corte com mozzarella di bufala e manjericão fresco", 11.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg/500px-Eq_it-na_pizza-margherita_sep2005_sml.jpg", 16, "Pizzeria Romana al Taglio", 4.6, 198),
        PratoDto(27, "Diavola", "Pizza", "Pizza picante com salame calabrês, pimento e mozzarella", 13.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Pepperoni_pizza.jpg/500px-Pepperoni_pizza.jpg", 16, "Pizzeria Romana al Taglio", 4.5, 145),

        // Pizza a Pezzi (Porto)
        PratoDto(28, "Margherita Napolitana", "Pizza", "Pizza napolitana com massa de 72h de fermentação e San Marzano DOP", 12.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg/500px-Eq_it-na_pizza-margherita_sep2005_sml.jpg", 17, "Pizza a Pezzi", 4.7, 267),
        PratoDto(29, "Quatro Formaggi", "Pizza", "Pizza com gorgonzola, mozzarella, parmigiano e fontina", 14.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Pizza_Margherita_stu_spivack.jpg/500px-Pizza_Margherita_stu_spivack.jpg", 17, "Pizza a Pezzi", 4.4, 134),

        // ─── Marisco ────────────────────────────────────────────────────

        // Cervejaria Ramiro
        PratoDto(4, "Gambas al Ajillo", "Marisco", "Gambas frescas salteadas em azeite com alho e malagueta", 18.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/Gambas_al_ajillo.jpg/500px-Gambas_al_ajillo.jpg", 3, "Cervejaria Ramiro", 4.8, 234, destacado = true),
        PratoDto(5, "Amêijoas à Bulhão Pato", "Marisco", "Amêijoas frescas com azeite, alho, coentros e limão", 22.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Am%C3%AAijoas_%C3%A0_Bulh%C3%A3o_Pato.jpg/500px-Am%C3%AAijoas_%C3%A0_Bulh%C3%A3o_Pato.jpg", 3, "Cervejaria Ramiro", 4.7, 187),
        PratoDto(6, "Lavagante Grelhado", "Marisco", "Lavagante fresco grelhado com manteiga de ervas", 45.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Grilled_Lobster_%288558909573%29.jpg/500px-Grilled_Lobster_%288558909573%29.jpg", 3, "Cervejaria Ramiro", 4.9, 145),

        // Sea Me (Lisboa)
        PratoDto(30, "Gambas à Sea Me", "Marisco", "Gambas gigantes grelhadas com manteiga de alho e ervas frescas", 21.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/Gambas_al_ajillo.jpg/500px-Gambas_al_ajillo.jpg", 18, "Sea Me", 4.7, 189),
        PratoDto(31, "Cataplana de Marisco", "Marisco", "Cataplana algarvia com amêijoas, camarão, lagostins e tamboril", 38.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Cataplana_de_zarzuela.jpg/500px-Cataplana_de_zarzuela.jpg", 18, "Sea Me", 4.8, 223),

        // Marisqueira do Lis (Lisboa)
        PratoDto(32, "Arroz de Marisco", "Marisco", "Arroz caldoso com camarão, amêijoas e berbigão, temperado com coentros", 24.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Arroz_de_marisco_in_Lisbon.jpg/500px-Arroz_de_marisco_in_Lisbon.jpg", 19, "Marisqueira do Lis", 4.6, 178),
        PratoDto(33, "Amêijoas à Marisqueira", "Marisco", "Amêijoas ao natural com azeite, alho e sumo de limão fresco", 19.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Am%C3%AAijoas_%C3%A0_Bulh%C3%A3o_Pato.jpg/500px-Am%C3%AAijoas_%C3%A0_Bulh%C3%A3o_Pato.jpg", 19, "Marisqueira do Lis", 4.5, 134),

        // ─── Francesinha ────────────────────────────────────────────────

        // Café Santiago (Porto)
        PratoDto(7, "Francesinha Especial", "Francesinha", "Francesinha com fiambre, linguiça, salsicha fresca, bife e ovo, coberta com queijo e molho especial", 14.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Francesinha_Sandwich_%28cropped%29.jpg/500px-Francesinha_Sandwich_%28cropped%29.jpg", 5, "Café Santiago", 4.9, 445, destacado = true),
        PratoDto(8, "Mini Francesinha", "Francesinha", "Versão mini da clássica francesinha do Porto", 9.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Francesinha_do_Porto.jpg/500px-Francesinha_do_Porto.jpg", 5, "Café Santiago", 4.6, 178),

        // Bufete Fase (Porto)
        PratoDto(21, "Francesinha Tradicional", "Francesinha", "Francesinha clássica do Porto com molho secreto da casa, fiambre, linguiça e bife", 13.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Francesinha_Sandwich_%28cropped%29.jpg/500px-Francesinha_Sandwich_%28cropped%29.jpg", 12, "Bufete Fase", 4.7, 320),
        PratoDto(22, "Francesinha com Camarão", "Francesinha", "Francesinha premium com camarão grelhado e molho de marisco", 17.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Francesinha_do_Porto.jpg/500px-Francesinha_do_Porto.jpg", 12, "Bufete Fase", 4.5, 112),

        // Cervejaria Gazela (Porto)
        PratoDto(23, "Francesinha da Gazela", "Francesinha", "A famosa francesinha da Gazela com molho picante e batata frita caseira", 12.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Francesinha_Sandwich_%28cropped%29.jpg/500px-Francesinha_Sandwich_%28cropped%29.jpg", 13, "Cervejaria Gazela", 4.8, 410),

        // A Cozinha do Manel (Lisboa)
        PratoDto(24, "Francesinha à Lisboeta", "Francesinha", "Versão lisboeta da francesinha com molho de cerveja artesanal e queijo da Serra", 15.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Francesinha_do_Porto.jpg/500px-Francesinha_do_Porto.jpg", 14, "A Cozinha do Manel", 4.3, 89),

        // O Velho Eurico (Lisboa)
        PratoDto(25, "Francesinha do Eurico", "Francesinha", "Francesinha com pão artesanal, alheira e molho de tomate com cerveja preta", 14.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Francesinha_Sandwich_%28cropped%29.jpg/500px-Francesinha_Sandwich_%28cropped%29.jpg", 15, "O Velho Eurico", 4.4, 156),

        // ─── Hambúrguer ─────────────────────────────────────────────────

        // Hamburgueria do Bairro (Lisboa)
        PratoDto(9, "Classic Smash Burger", "Hambúrguer", "Dois smash patties de 90g, queijo cheddar, pickles, cebola caramelizada e molho especial", 12.50, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&q=80&w=400", 9, "Hamburgueria do Bairro", 4.7, 201, destacado = true),
        PratoDto(10, "Bacon Cheese Burger", "Hambúrguer", "Burger 180g com bacon crocante, queijo cheddar derretido, alface e tomate", 14.00, "https://images.unsplash.com/photo-1553979459-d2229ba7433b?auto=format&fit=crop&q=80&w=400", 9, "Hamburgueria do Bairro", 4.5, 156),
        PratoDto(11, "Truffle Burger", "Hambúrguer", "Burger 200g com queijo brie, rúcula, cogumelos e maionese de trufa", 16.00, "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?auto=format&fit=crop&q=80&w=400", 9, "Hamburgueria do Bairro", 4.8, 89),

        // Tasca do Chico (Lisboa)
        PratoDto(34, "Burger da Tasca", "Hambúrguer", "Hambúrguer artesanal 200g com queijo de cabra, rúcula e cebola roxa caramelizada", 13.50, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&q=80&w=400", 20, "Tasca do Chico", 4.6, 167),
        PratoDto(35, "Smash Burger Duplo", "Hambúrguer", "Dois smash patties com cheddar fumado, pickles e mostarda dijon", 14.50, "https://images.unsplash.com/photo-1553979459-d2229ba7433b?auto=format&fit=crop&q=80&w=400", 20, "Tasca do Chico", 4.5, 145),

        // ─── Sushi ──────────────────────────────────────────────────────

        // Sushicafé Avenida (Lisboa)
        PratoDto(12, "Nigiri de Salmão (6 pcs)", "Sushi", "Seis peças de nigiri com salmão fresco do Atlântico", 14.00, "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?auto=format&fit=crop&q=80&w=400", 10, "Sushicafé Avenida", 4.6, 134),
        PratoDto(13, "Dragon Roll (8 pcs)", "Sushi", "Roll especial com camarão tempura, abacate, enguia e tobiko", 16.50, "https://images.unsplash.com/photo-1617196034796-73dfa7b1fd56?auto=format&fit=crop&q=80&w=400", 10, "Sushicafé Avenida", 4.7, 98),
        PratoDto(14, "Sashimi Misto", "Sushi", "Seleção de 15 fatias de sashimi: salmão, atum e robalo", 22.00, "https://images.unsplash.com/photo-1534482421-64566f976cfa?auto=format&fit=crop&q=80&w=400", 10, "Sushicafé Avenida", 4.8, 167, destacado = true),

        // Confraria do Sushi (Lisboa)
        PratoDto(36, "Sashimi Premium", "Sushi", "20 fatias de sashimi premium: salmão norueguês, atum rabilho e peixe-manteiga", 28.00, "https://images.unsplash.com/photo-1534482421-64566f976cfa?auto=format&fit=crop&q=80&w=400", 21, "Confraria do Sushi", 4.8, 201),
        PratoDto(37, "Temaki de Salmão", "Sushi", "Temaki crocante com salmão fresco, cream cheese e cebolinho", 9.50, "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?auto=format&fit=crop&q=80&w=400", 21, "Confraria do Sushi", 4.5, 156),

        // ─── Pasta ──────────────────────────────────────────────────────

        // Pasta Non Basta (Lisboa)
        PratoDto(15, "Carbonara", "Pasta", "Spaghetti com guanciale, ovo, pecorino romano e pimenta preta", 13.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Espaguetis_carbonara.jpg/500px-Espaguetis_carbonara.jpg", 11, "Pasta Non Basta", 4.5, 99),
        PratoDto(16, "Lasagna della Casa", "Pasta", "Lasanha tradicional com ragù de carne, béchamel e parmigiano", 13.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Lasagna_%281%29.jpg/500px-Lasagna_%281%29.jpg", 11, "Pasta Non Basta", 4.6, 112),
        PratoDto(17, "Penne all'Arrabbiata", "Pasta", "Penne com molho de tomate picante, alho e salsa", 11.50, "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?auto=format&fit=crop&q=80&w=400", 11, "Pasta Non Basta", 4.3, 78),

        // Trattoria Amadora (Lisboa)
        PratoDto(38, "Cacio e Pepe", "Pasta", "Tonnarelli al cacio e pepe com pecorino romano DOP e pimenta preta", 14.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Espaguetis_carbonara.jpg/500px-Espaguetis_carbonara.jpg", 22, "Trattoria Amadora", 4.7, 134),
        PratoDto(39, "Ravioli di Ricotta", "Pasta", "Ravioli caseiro recheado com ricotta e espinafres, molho de manteiga e sálvia", 15.50, "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?auto=format&fit=crop&q=80&w=400", 22, "Trattoria Amadora", 4.6, 98),

        // ─── Sobremesas ─────────────────────────────────────────────────

        // Pastéis de Belém (Lisboa)
        PratoDto(18, "Pastel de Belém", "Sobremesas", "O original pastel de nata de Belém com canela e açúcar em pó", 1.30, "https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Pastel_de_nata_%2818616473070%29.jpg/500px-Pastel_de_nata_%2818616473070%29.jpg", 7, "Pastéis de Belém", 4.9, 523, destacado = true),
        PratoDto(19, "Tarte de Amêndoa", "Sobremesas", "Tarte crocante de amêndoa do Algarve com gelado de baunilha", 4.50, "https://images.unsplash.com/photo-1519915028121-7d3463d20b13?auto=format&fit=crop&q=80&w=400", 7, "Pastéis de Belém", 4.5, 198),
        PratoDto(20, "Mousse de Chocolate", "Sobremesas", "Mousse de chocolate negro belga com raspas de chocolate", 5.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Chocolate_mousse_%2816013444604%29.jpg/500px-Chocolate_mousse_%2816013444604%29.jpg", 7, "Pastéis de Belém", 4.7, 145),

        // Nata Lisboa (Lisboa)
        PratoDto(40, "Pastel de Nata Artesanal", "Sobremesas", "Pastel de nata artesanal com massa folhada crocante e creme de ovos", 1.80, "https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Pastel_de_nata_%2818616473070%29.jpg/500px-Pastel_de_nata_%2818616473070%29.jpg", 23, "Nata Lisboa", 4.6, 312),
        PratoDto(41, "Bolo de Bolacha", "Sobremesas", "Bolo de bolacha maria com creme de café e chocolate", 3.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9f/Bolo_de_bolacha.jpg/500px-Bolo_de_bolacha.jpg", 23, "Nata Lisboa", 4.4, 89),

        // ─── Bifana (NOVA CATEGORIA) ────────────────────────────────────

        // Casa das Bifanas (Lisboa)
        PratoDto(42, "Bifana Clássica", "Bifana", "Bifana tradicional com febras de porco marinadas em alho, vinho e piri-piri", 4.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Bifana_on_a_plate.jpg/500px-Bifana_on_a_plate.jpg", 24, "Casa das Bifanas", 4.7, 356),
        PratoDto(43, "Bifana com Queijo", "Bifana", "Bifana com queijo da Serra derretido e mostarda dijon", 5.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Bifana_on_a_plate.jpg/500px-Bifana_on_a_plate.jpg", 24, "Casa das Bifanas", 4.5, 198),

        // O Trevo (Porto)
        PratoDto(44, "Bifana do Porto", "Bifana", "Bifana à moda do Porto com molho picante e pão crocante", 4.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Porto-style_Bifana_sandwich.jpg/500px-Porto-style_Bifana_sandwich.jpg", 25, "O Trevo", 4.8, 412),
        PratoDto(45, "Prego no Pão", "Bifana", "Bife de vaca tenro no pão com manteiga de alho", 5.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Bifana_on_a_plate.jpg/500px-Bifana_on_a_plate.jpg", 25, "O Trevo", 4.6, 234),

        // Conga (Porto)
        PratoDto(46, "Bifana da Conga", "Bifana", "A famosa bifana da Conga com molho secreto da casa desde 1976", 3.80, "https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Porto-style_Bifana_sandwich.jpg/500px-Porto-style_Bifana_sandwich.jpg", 26, "Conga", 4.9, 567),

        // Cervejaria Ramiro (Lisboa) — bifana também!
        PratoDto(47, "Bifana do Ramiro", "Bifana", "Bifana gourmet com carne de porco ibérico e molho de cerveja", 6.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Cervejaria_Ramiro_%2841171394540%29.jpg/500px-Cervejaria_Ramiro_%2841171394540%29.jpg", 3, "Cervejaria Ramiro", 4.6, 189),

        // ─── Bacalhau (NOVA CATEGORIA) ──────────────────────────────────

        // Solar dos Presuntos (Lisboa)
        PratoDto(48, "Bacalhau à Brás", "Bacalhau", "Bacalhau desfiado com batata palha, cebola e ovo mexido", 18.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Bacalhau_a_Bras.jpg/500px-Bacalhau_a_Bras.jpg", 27, "Solar dos Presuntos", 4.8, 389),
        PratoDto(49, "Bacalhau com Natas", "Bacalhau", "Bacalhau gratinado com natas, batata e cebola caramelizada", 17.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Bacalhau_com_natas.jpg/500px-Bacalhau_com_natas.jpg", 27, "Solar dos Presuntos", 4.7, 245),

        // Laurentina (Lisboa)
        PratoDto(50, "Bacalhau à Lagareiro", "Bacalhau", "Lombo de bacalhau assado no forno com batata a murro e azeite de Trás-os-Montes", 22.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Bacalhau_a_Lagareiro_%2850762086577%29.jpg/500px-Bacalhau_a_Lagareiro_%2850762086577%29.jpg", 28, "Laurentina", 4.9, 312),
        PratoDto(51, "Bacalhau à Brás Premium", "Bacalhau", "Versão premium com bacalhau da Noruega e azeite DOP", 21.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Bacalhau_a_Bras.jpg/500px-Bacalhau_a_Bras.jpg", 28, "Laurentina", 4.7, 198),

        // O Gaveto (Porto)
        PratoDto(52, "Bacalhau à Gomes de Sá", "Bacalhau", "Bacalhau com batata cozida, cebola, ovo cozido e azeitonas", 19.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Bacalhau_%C3%A0_Gomes_de_S%C3%A1.jpg/500px-Bacalhau_%C3%A0_Gomes_de_S%C3%A1.jpg", 29, "O Gaveto", 4.6, 267),
        PratoDto(53, "Bacalhau à Brás do Gaveto", "Bacalhau", "Bacalhau à Brás com batata palha crocante e azeitonas de Elvas", 18.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Bacalhau_a_Bras.jpg/500px-Bacalhau_a_Bras.jpg", 29, "O Gaveto", 4.5, 178),

        // ─── Novos restaurantes — ronda 2 ──────────────────────────────

        // Zero Zero (Lisboa) — Pizza
        PratoDto(54, "Pizza Quattro Stagioni", "Pizza", "Pizza com alcachofras, cogumelos, azeitonas e presunto, dividida em quatro partes", 14.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Pizza_quattro_stagioni.jpg/500px-Pizza_quattro_stagioni.jpg", 30, "Zero Zero", 4.7, 289),
        PratoDto(55, "Calzone Frito", "Pizza", "Calzone frito recheado com ricotta, mozzarella e salame napolitano", 13.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/5/54/Calzone_fritto.jpg/500px-Calzone_fritto.jpg", 30, "Zero Zero", 4.5, 167),

        // Marisqueira Nunes (Lisboa) — Marisco
        PratoDto(56, "Polvo Grelhado", "Marisco", "Polvo grelhado com batata a murro, azeite e alho", 26.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Grilled_octopus.jpg/500px-Grilled_octopus.jpg", 31, "Marisqueira Nunes", 4.8, 312),
        PratoDto(57, "Sapateira Recheada", "Marisco", "Sapateira fresca recheada com o seu próprio coral e maionese caseira", 28.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/Gambas_al_ajillo.jpg/500px-Gambas_al_ajillo.jpg", 31, "Marisqueira Nunes", 4.6, 198),

        // Lado B (Porto) — Francesinha
        PratoDto(58, "Francesinha Premium", "Francesinha", "Francesinha gourmet com carne de vitela, presunto ibérico e molho de cerveja artesanal", 16.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Francesinha_Sandwich_%28cropped%29.jpg/500px-Francesinha_Sandwich_%28cropped%29.jpg", 32, "Lado B", 4.7, 234),
        PratoDto(59, "Mini Francesinha Veggie", "Francesinha", "Mini francesinha vegetariana com cogumelos, queijo e molho especial", 11.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Francesinha_do_Porto.jpg/500px-Francesinha_do_Porto.jpg", 32, "Lado B", 4.3, 89),

        // The Good Burger (Lisboa) — Hambúrguer
        PratoDto(60, "Wagyu Burger", "Hambúrguer", "Hambúrguer de wagyu A5 200g com foie gras, rúcula e trufa negra", 22.00, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&q=80&w=400", 33, "The Good Burger", 4.9, 178),
        PratoDto(61, "Chicken Crunch Burger", "Hambúrguer", "Frango panado crocante com coleslaw, pickles e molho ranch", 13.00, "https://images.unsplash.com/photo-1553979459-d2229ba7433b?auto=format&fit=crop&q=80&w=400", 33, "The Good Burger", 4.5, 201),

        // Kanazawa (Lisboa) — Sushi
        PratoDto(62, "Omakase 12 Peças", "Sushi", "Seleção do chef com 12 peças de nigiri premium do dia", 38.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/60/Sushi_platter.jpg/500px-Sushi_platter.jpg", 34, "Kanazawa", 4.9, 267),
        PratoDto(63, "Tuna Tataki", "Sushi", "Tataki de atum rabilho com ponzu, cebolinho e gergelim torrado", 18.50, "https://images.unsplash.com/photo-1534482421-64566f976cfa?auto=format&fit=crop&q=80&w=400", 34, "Kanazawa", 4.7, 156),

        // Paparico (Porto) — Pasta
        PratoDto(64, "Tagliatelle al Ragù", "Pasta", "Tagliatelle fresco com ragù bolonhês cozinhado 6 horas", 15.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Espaguetis_carbonara.jpg/500px-Espaguetis_carbonara.jpg", 35, "Paparico", 4.8, 198),
        PratoDto(65, "Risotto ai Funghi Porcini", "Pasta", "Risotto cremoso com cogumelos porcini frescos e parmigiano 24 meses", 16.50, "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?auto=format&fit=crop&q=80&w=400", 35, "Paparico", 4.6, 134),

        // Manteigaria (Lisboa) — Sobremesas
        PratoDto(66, "Pastel de Nata da Manteigaria", "Sobremesas", "Pastel de nata artesanal feito à vista com massa de 72 camadas", 1.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Pastel_de_nata_%2818616473070%29.jpg/500px-Pastel_de_nata_%2818616473070%29.jpg", 36, "Manteigaria", 4.8, 445),
        PratoDto(67, "Tiramisu", "Sobremesas", "Tiramisu italiano com mascarpone, café expresso e cacau", 5.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Tiramisu_dessert.jpg/500px-Tiramisu_dessert.jpg", 36, "Manteigaria", 4.6, 167),

        // As Bifanas do Afonso (Lisboa) — Bifana
        PratoDto(68, "Bifana do Afonso", "Bifana", "Bifana com carne de porco preto alentejano marinada 24h em vinha d'alhos", 5.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Bifana_on_a_plate.jpg/500px-Bifana_on_a_plate.jpg", 37, "As Bifanas do Afonso", 4.8, 289),
        PratoDto(69, "Prego Especial", "Bifana", "Prego no pão com bife de vaca maturada, mostarda e rúcula", 6.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Porto-style_Bifana_sandwich.jpg/500px-Porto-style_Bifana_sandwich.jpg", 37, "As Bifanas do Afonso", 4.6, 198),

        // Adega São Nicolau (Porto) — Bacalhau
        PratoDto(70, "Bacalhau à São Nicolau", "Bacalhau", "Bacalhau assado com batata a murro, grelos e azeite DOP do Douro", 20.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Bacalhau_a_Lagareiro_%2850762086577%29.jpg/500px-Bacalhau_a_Lagareiro_%2850762086577%29.jpg", 38, "Adega São Nicolau", 4.7, 234),
        PratoDto(71, "Bacalhau com Natas do Porto", "Bacalhau", "Bacalhau com natas gratinado no forno a lenha com batata e cebola", 18.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Bacalhau_com_natas.jpg/500px-Bacalhau_com_natas.jpg", 38, "Adega São Nicolau", 4.6, 189),

        // ─── Novos pratos — ronda 3 (restaurantes novos, tipos existentes) ──

        // Forno d'Oro (Lisboa) — Pizza
        PratoDto(72, "Margherita Forno d'Oro", "Pizza", "Pizza margherita com massa artesanal fermentada 48h e mozzarella di bufala", 12.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg/500px-Eq_it-na_pizza-margherita_sep2005_sml.jpg", 39, "Forno d'Oro", 4.7, 234),
        PratoDto(73, "Pepperoni Forno d'Oro", "Pizza", "Pizza com pepperoni artesanal e mozzarella fior di latte", 14.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Pepperoni_pizza.jpg/500px-Pepperoni_pizza.jpg", 39, "Forno d'Oro", 4.6, 189),

        // Pizzeria Porto Belo (Porto) — Pizza
        PratoDto(74, "Margherita Porto Belo", "Pizza", "Pizza margherita napolitana com tomate San Marzano e manjericão fresco", 11.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg/500px-Eq_it-na_pizza-margherita_sep2005_sml.jpg", 40, "Pizzeria Porto Belo", 4.5, 178),
        PratoDto(75, "Diavola Porto Belo", "Pizza", "Pizza diavola com salame picante calabrês e pimento assado", 12.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Pepperoni_pizza.jpg/500px-Pepperoni_pizza.jpg", 40, "Pizzeria Porto Belo", 4.4, 145),

        // Marisqueira do Douro (Porto) — Marisco
        PratoDto(76, "Gambas do Douro", "Marisco", "Gambas grelhadas com azeite de alho e limão do Algarve", 19.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/Gambas_al_ajillo.jpg/500px-Gambas_al_ajillo.jpg", 41, "Marisqueira do Douro", 4.6, 198),
        PratoDto(77, "Amêijoas do Douro", "Marisco", "Amêijoas frescas à Bulhão Pato com coentros e azeite virgem", 17.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Am%C3%AAijoas_%C3%A0_Bulh%C3%A3o_Pato.jpg/500px-Am%C3%AAijoas_%C3%A0_Bulh%C3%A3o_Pato.jpg", 41, "Marisqueira do Douro", 4.5, 156),
        PratoDto(78, "Arroz de Marisco do Douro", "Marisco", "Arroz caldoso com marisco fresco do Atlântico e coentros", 22.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Arroz_de_marisco_in_Lisbon.jpg/500px-Arroz_de_marisco_in_Lisbon.jpg", 41, "Marisqueira do Douro", 4.7, 234),

        // Burger Lovers Porto (Porto) — Hambúrguer
        PratoDto(79, "Smash Burger Porto", "Hambúrguer", "Duplo smash patty com cheddar curado, cebola roxa e molho da casa", 11.50, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&q=80&w=400", 42, "Burger Lovers Porto", 4.6, 267),
        PratoDto(80, "Bacon Cheese Porto", "Hambúrguer", "Burger 200g com bacon fumado do Minho, cheddar e pickles caseiros", 13.00, "https://images.unsplash.com/photo-1553979459-d2229ba7433b?auto=format&fit=crop&q=80&w=400", 42, "Burger Lovers Porto", 4.5, 198),

        // Sushi Sato (Porto) — Sushi
        PratoDto(81, "Sashimi do Atlântico", "Sushi", "18 fatias de sashimi: salmão, atum rabilho e peixe-espada", 24.00, "https://images.unsplash.com/photo-1534482421-64566f976cfa?auto=format&fit=crop&q=80&w=400", 43, "Sushi Sato", 4.7, 189),
        PratoDto(82, "Nigiri Salmão Premium", "Sushi", "8 peças de nigiri com salmão fresco e arroz temperado com vinagre de arroz", 15.00, "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?auto=format&fit=crop&q=80&w=400", 43, "Sushi Sato", 4.6, 156),

        // Trattoria Porto (Porto) — Pasta
        PratoDto(83, "Carbonara do Porto", "Pasta", "Spaghetti carbonara com guanciale importado e pecorino romano DOP", 14.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Espaguetis_carbonara.jpg/500px-Espaguetis_carbonara.jpg", 44, "Trattoria Porto", 4.7, 201),
        PratoDto(84, "Lasagna Caseira", "Pasta", "Lasanha caseira com ragù de vitela, béchamel e parmigiano reggiano", 14.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Lasagna_%281%29.jpg/500px-Lasagna_%281%29.jpg", 44, "Trattoria Porto", 4.6, 167),

        // Nata Pura (Porto) — Sobremesas
        PratoDto(85, "Pastel de Nata do Porto", "Sobremesas", "Pastel de nata artesanal com massa folhada crocante e canela", 1.60, "https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Pastel_de_nata_%2818616473070%29.jpg/500px-Pastel_de_nata_%2818616473070%29.jpg", 45, "Nata Pura", 4.7, 356),
        PratoDto(86, "Mousse de Chocolate Porto", "Sobremesas", "Mousse de chocolate negro com flor de sal e avelãs torradas", 4.80, "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Chocolate_mousse_%2816013444604%29.jpg/500px-Chocolate_mousse_%2816013444604%29.jpg", 45, "Nata Pura", 4.5, 134),

        // O Bacalhoeiro (Lisboa) — Bacalhau
        PratoDto(87, "Bacalhau à Gomes de Sá do Alfama", "Bacalhau", "Bacalhau à Gomes de Sá com batata, cebola, ovo e azeitonas de Elvas", 19.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Bacalhau_%C3%A0_Gomes_de_S%C3%A1.jpg/500px-Bacalhau_%C3%A0_Gomes_de_S%C3%A1.jpg", 46, "O Bacalhoeiro", 4.7, 278),
        PratoDto(88, "Bacalhau à Lagareiro do Alfama", "Bacalhau", "Lombo de bacalhau assado com batata a murro, azeite e alho", 21.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Bacalhau_a_Lagareiro_%2850762086577%29.jpg/500px-Bacalhau_a_Lagareiro_%2850762086577%29.jpg", 46, "O Bacalhoeiro", 4.8, 312),

        // ─── Pratos Tradicionais ─────────────────────────────────────────

        // Solar dos Presuntos (Lisboa)
        PratoDto(89, "Cozido à Portuguesa", "Pratos Tradicionais", "Cozido tradicional com enchidos, carnes, couves e legumes frescos da época", 22.50, "https://commons.wikimedia.org/wiki/Special:Redirect/file/Cozido%20a%20portuguesa%201.JPG", 27, "Solar dos Presuntos", 4.9, 412),
        PratoDto(90, "Arroz de Pato", "Pratos Tradicionais", "Arroz de pato no forno com chouriço, presunto e hortelã", 18.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Arroz_de_pato_no_forno.jpg/500px-Arroz_de_pato_no_forno.jpg", 27, "Solar dos Presuntos", 4.8, 345),
        // Tasca do Chico (Lisboa)
        PratoDto(91, "Açorda Alentejana", "Pratos Tradicionais", "Açorda de coentros com ovo escalfado, azeite e alho", 14.00, "https://commons.wikimedia.org/wiki/Special:Redirect/file/A%C3%A7orda%20%C3%A0%20Alentejana.jpg", 20, "Tasca do Chico", 4.6, 198),
        PratoDto(92, "Secretos de Porco Preto", "Pratos Tradicionais", "Secretos de porco preto ibérico com batata-doce assada e grelos salteados", 17.50, "https://images.unsplash.com/photo-1432139555190-58524dae6a55?auto=format&fit=crop&q=80&w=400", 20, "Tasca do Chico", 4.7, 267),
        // A Cozinha do Manel (Lisboa)
        PratoDto(93, "Feijoada à Transmontana", "Pratos Tradicionais", "Feijoada de feijão branco com enchidos, carnes fumadas e couve", 16.50, "https://commons.wikimedia.org/wiki/Special:Redirect/file/Feijoada%20%C3%A0%20transmontada.jpg", 14, "A Cozinha do Manel", 4.7, 234),
        // Paparico (Porto)
        PratoDto(94, "Rojões à Minhota", "Pratos Tradicionais", "Rojões de porco bísaro com castanhas, batata frita e arroz de sarrabulho", 16.00, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Roj%C3%B5es.jpg/500px-Roj%C3%B5es.jpg", 35, "Paparico", 4.8, 289),
        PratoDto(95, "Tripas à Moda do Porto", "Pratos Tradicionais", "Tripas com feijão branco, enchidos e especiarias — o prato emblemático do Porto", 15.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Tripas_%C3%A0_moda_do_Porto.jpg/500px-Tripas_%C3%A0_moda_do_Porto.jpg", 35, "Paparico", 4.6, 178),
        // Adega São Nicolau (Porto)
        PratoDto(96, "Arroz de Cabidela", "Pratos Tradicionais", "Arroz de cabidela de frango com vinagre e sangue, receita tradicional", 14.50, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Arroz_de_cabidela.jpg/500px-Arroz_de_cabidela.jpg", 38, "Adega São Nicolau", 4.5, 156)
    )

    val pratos: List<PratoDto> get() = _pratos

    // ─── Agrupamento por tipo de prato (para comparação) ────────────────

    /**
     * Mapeamento pratoId → tipo canónico.
     * Pratos do mesmo tipo em restaurantes diferentes partilham o mesmo tipo.
     */
    val pratoTipos: Map<Int, String> = mapOf(
        // Pizza
        1 to "Margherita", 2 to "Pepperoni", 3 to "Funghi",
        26 to "Margherita", 27 to "Diavola",
        28 to "Margherita", 29 to "Quatro Formaggi",
        54 to "Quattro Stagioni", 55 to "Calzone",
        // Marisco
        4 to "Gambas", 5 to "Amêijoas", 6 to "Lavagante Grelhado",
        30 to "Gambas", 31 to "Cataplana de Marisco",
        32 to "Arroz de Marisco", 33 to "Amêijoas",
        56 to "Polvo Grelhado", 57 to "Sapateira Recheada",
        // Francesinha
        7 to "Francesinha", 8 to "Mini Francesinha",
        21 to "Francesinha", 22 to "Francesinha com Camarão",
        23 to "Francesinha", 24 to "Francesinha", 25 to "Francesinha",
        58 to "Francesinha", 59 to "Mini Francesinha",
        // Hambúrguer
        9 to "Smash Burger", 10 to "Bacon Cheese Burger", 11 to "Truffle Burger",
        34 to "Burger Artesanal", 35 to "Smash Burger",
        60 to "Wagyu Burger", 61 to "Chicken Burger",
        // Sushi
        12 to "Nigiri de Salmão", 13 to "Dragon Roll", 14 to "Sashimi",
        36 to "Sashimi", 37 to "Temaki de Salmão",
        62 to "Omakase", 63 to "Tuna Tataki",
        // Pasta
        15 to "Carbonara", 16 to "Lasagna", 17 to "Penne all'Arrabbiata",
        38 to "Cacio e Pepe", 39 to "Ravioli di Ricotta",
        64 to "Tagliatelle al Ragù", 65 to "Risotto ai Funghi",
        // Sobremesas
        18 to "Pastel de Nata", 19 to "Tarte de Amêndoa", 20 to "Mousse de Chocolate",
        40 to "Pastel de Nata", 41 to "Bolo de Bolacha",
        66 to "Pastel de Nata", 67 to "Tiramisu",
        // Bifana
        42 to "Bifana", 43 to "Bifana com Queijo",
        44 to "Bifana", 45 to "Prego no Pão",
        46 to "Bifana", 47 to "Bifana",
        68 to "Bifana", 69 to "Prego no Pão",
        // Bacalhau
        48 to "Bacalhau à Brás", 49 to "Bacalhau com Natas",
        50 to "Bacalhau à Lagareiro", 51 to "Bacalhau à Brás",
        52 to "Bacalhau à Gomes de Sá", 53 to "Bacalhau à Brás",
        70 to "Bacalhau à Lagareiro", 71 to "Bacalhau com Natas",
        // Novos — ronda 3
        72 to "Margherita", 73 to "Pepperoni",
        74 to "Margherita", 75 to "Diavola",
        76 to "Gambas", 77 to "Amêijoas", 78 to "Arroz de Marisco",
        79 to "Smash Burger", 80 to "Bacon Cheese Burger",
        81 to "Sashimi", 82 to "Nigiri de Salmão",
        83 to "Carbonara", 84 to "Lasagna",
        85 to "Pastel de Nata", 86 to "Mousse de Chocolate",
        87 to "Bacalhau à Gomes de Sá", 88 to "Bacalhau à Lagareiro",
        // Pratos Tradicionais
        89 to "Cozido à Portuguesa", 90 to "Arroz de Pato",
        91 to "Açorda Alentejana", 92 to "Secretos de Porco Preto",
        93 to "Feijoada à Transmontana",
        94 to "Rojões à Minhota", 95 to "Tripas à Moda do Porto",
        96 to "Arroz de Cabidela"
    )

    fun getPratoTipo(pratoId: Int): String =
        pratoTipos[pratoId] ?: pratos.find { it.id == pratoId }?.nome ?: ""

    fun getPratosByTipo(tipo: String, cidade: String = "Todas"): List<PratoDto> {
        val filtered = pratos.filter { pratoTipos[it.id] == tipo }
        val byCity = if (cidade == "Todas" || cidade.isBlank()) {
            filtered
        } else {
            filtered.filter { prato ->
                restaurantes.find { it.id == prato.restauranteId }?.cidade == cidade
            }
        }
        return byCity.sortedByDescending { it.ratingMedio }
    }

    /**
     * Deduplica pratos por tipo — retorna um representante por tipo de prato.
     * O representante usa a melhor imagem/rating, soma avaliações,
     * e mostra "X restaurantes" se disponível em múltiplos restaurantes.
     */
    fun deduplicatePratos(source: List<PratoDto>): List<PratoDto> {
        return source.groupBy { pratoTipos[it.id] ?: it.nome }
            .map { (tipo, group) ->
                val best = group.maxByOrNull { it.ratingMedio } ?: group.first()
                val totalAval = group.sumOf { it.totalAvaliacoes }
                val avgRating = Math.round(group.map { it.ratingMedio }.average() * 10) / 10.0
                val restCount = group.map { it.restauranteId }.distinct().size
                val minPrice = group.mapNotNull { it.preco }.minOrNull()
                best.copy(
                    nome = tipo,
                    restauranteNome = if (restCount > 1) "$restCount restaurantes" else best.restauranteNome,
                    totalAvaliacoes = totalAval,
                    ratingMedio = avgRating,
                    preco = minPrice
                )
            }
            .sortedByDescending { it.ratingMedio }
    }

    // ─── Detalhes dos Pratos ────────────────────────────────────────────

    fun getPratoDetail(id: Int): PratoDetailDto? {
        val prato = pratos.find { it.id == id } ?: return null
        val rest = restaurantes.find { it.id == prato.restauranteId } ?: return null
        return PratoDetailDto(
            id = prato.id,
            nome = prato.nome,
            categoria = prato.categoria,
            descricao = prato.descricao,
            preco = prato.preco,
            imagemUrl = prato.imagemUrl,
            disponivel = true,
            restauranteId = rest.id,
            restauranteNome = rest.nome,
            restauranteMorada = "${rest.morada}, ${rest.cidade}",
            restauranteLatitude = rest.latitude,
            restauranteLongitude = rest.longitude,
            ratingMedio = prato.ratingMedio,
            totalAvaliacoes = prato.totalAvaliacoes
        )
    }

    // ─── Avaliações Mock ────────────────────────────────────────────────

    private val _avaliacoes: MutableMap<Int, MutableList<AvaliacaoDto>> = mapOf(
        1 to listOf(
            AvaliacaoDto(101, 10, "Maria S.", 5, "A melhor bifana que já comi! Carne tenra e pão quentinho.", "2025-03-15", null, null, null, null),
            AvaliacaoDto(102, 11, "João P.", 4, "Muito boa, mas achei o pão um pouco seco.", "2025-03-10", null, 201, "Obrigado João! Vamos melhorar o pão.", "2025-03-11"),
            AvaliacaoDto(103, 12, "Ana R.", 5, "Venho aqui todas as semanas. Nunca desilude!", "2025-02-28", null, null, null, null)
        ),
        3 to listOf(
            AvaliacaoDto(104, 10, "Carlos M.", 5, "Molho de cerveja preta é genial! Nota 10.", "2025-04-01", null, null, null, null),
            AvaliacaoDto(105, 13, "Sofia L.", 5, "Vale a viagem ao Porto só por esta bifana.", "2025-03-22", null, null, null, null)
        ),
        5 to listOf(
            AvaliacaoDto(106, 10, "Pedro G.", 5, "A MELHOR francesinha do mundo. Ponto final.", "2025-04-05", null, null, null, null),
            AvaliacaoDto(107, 14, "Rita C.", 4, "Muito boa, mas a fila de espera é enorme.", "2025-03-28", null, 202, "Obrigado Rita! Recomendamos reservar mesa.", "2025-03-29"),
            AvaliacaoDto(108, 15, "Miguel A.", 5, "Molho é absurdamente bom. Voltarei sempre.", "2025-03-20", null, null, null, null),
            AvaliacaoDto(109, 11, "Beatriz F.", 4, "Muito saborosa. Porções generosas!", "2025-03-15", null, null, null, null)
        ),
        7 to listOf(
            AvaliacaoDto(110, 12, "Tiago R.", 5, "É a francesinha que toda a gente procura.", "2025-04-08", null, null, null, null)
        ),
        11 to listOf(
            AvaliacaoDto(111, 13, "Luísa M.", 5, "O melhor Bacalhau à Brás de Lisboa!", "2025-03-18", null, null, null, null),
            AvaliacaoDto(112, 10, "André S.", 4, "Muito bom. Batata palha sempre crocante.", "2025-03-12", null, null, null, null)
        ),
        17 to listOf(
            AvaliacaoDto(113, 10, "Emma W.", 5, "Best pastéis de nata in the world!", "2025-04-10", null, null, null, null),
            AvaliacaoDto(114, 14, "François D.", 5, "Magnifique! A must-visit in Lisbon.", "2025-04-07", null, null, null, null),
            AvaliacaoDto(115, 11, "Clara V.", 5, "Quentinhos e crocantes. Perfeição.", "2025-04-02", null, null, null, null),
            AvaliacaoDto(116, 15, "Hugo T.", 4, "Excelentes, mas a fila é insuportável.", "2025-03-30", null, 203, "Sugerimos vir de manhã cedo! Obrigado.", "2025-03-31"),
            AvaliacaoDto(117, 12, "Sara N.", 5, "O melhor pastel de nata do universo.", "2025-03-25", null, null, null, null)
        ),
        20 to listOf(
            AvaliacaoDto(118, 13, "Rui B.", 5, "Pele estaladiça como deve ser! Magnífico.", "2025-04-06", null, null, null, null),
            AvaliacaoDto(119, 10, "Inês P.", 5, "Vale cada cêntimo. Carne suculenta.", "2025-03-29", null, null, null, null)
        ),
        23 to listOf(
            AvaliacaoDto(120, 14, "Diogo L.", 5, "Amêijoas perfeitas! Coentros na dose certa.", "2025-04-09", null, null, null, null),
            AvaliacaoDto(121, 15, "Catarina F.", 4, "Muito frescas. Preço justo para a qualidade.", "2025-04-03", null, null, null, null),
            AvaliacaoDto(122, 11, "Nuno G.", 5, "A Ramiro nunca falha. Sempre TOP.", "2025-03-26", null, null, null, null)
        ),
        26 to listOf(
            AvaliacaoDto(123, 12, "Mariana C.", 4, "Boa opção vegetariana em Lisboa.", "2025-04-04", null, null, null, null),
            AvaliacaoDto(124, 10, "Tomás H.", 5, "Surpreendente! Sabores muito bem combinados.", "2025-03-31", null, null, null, null)
        ),
        // Novos pratos — avaliações
        21 to listOf(
            AvaliacaoDto(125, 10, "Rui M.", 5, "A melhor francesinha do Porto! Molho incrível.", "2025-04-11", null, null, null, null),
            AvaliacaoDto(126, 14, "Helena B.", 4, "Muito boa, porção generosa.", "2025-04-05", null, null, null, null)
        ),
        23 to listOf(
            AvaliacaoDto(127, 11, "Vasco P.", 5, "Gazela nunca falha! Francesinha perfeita.", "2025-04-12", null, null, null, null),
            AvaliacaoDto(128, 12, "Marta S.", 5, "Fui lá 3 vezes numa semana. Vício!", "2025-04-08", null, null, null, null),
            AvaliacaoDto(129, 15, "Fernando R.", 4, "Boa francesinha, molho picante nota 10.", "2025-04-02", null, null, null, null)
        ),
        24 to listOf(
            AvaliacaoDto(130, 13, "Sandra L.", 4, "Boa francesinha em Lisboa. Molho diferente.", "2025-04-09", null, null, null, null)
        ),
        25 to listOf(
            AvaliacaoDto(131, 10, "Bruno C.", 4, "Pão artesanal faz a diferença. Recomendo.", "2025-04-07", null, null, null, null),
            AvaliacaoDto(132, 14, "Joana F.", 5, "Surpreendeu-me! Alheira na francesinha é genial.", "2025-04-01", null, null, null, null)
        ),
        28 to listOf(
            AvaliacaoDto(133, 11, "Afonso G.", 5, "Massa de 72h nota-se! Melhor pizza do Porto.", "2025-04-10", null, null, null, null),
            AvaliacaoDto(134, 15, "Leonor V.", 4, "Ingredientes de qualidade. Preço justo.", "2025-04-06", null, null, null, null)
        ),
        30 to listOf(
            AvaliacaoDto(135, 12, "Diogo T.", 5, "Gambas enormes e muito saborosas!", "2025-04-11", null, null, null, null)
        ),
        31 to listOf(
            AvaliacaoDto(136, 10, "Patrícia N.", 5, "Cataplana espetacular. Sabores do Algarve.", "2025-04-09", null, null, null, null),
            AvaliacaoDto(137, 13, "Ricardo A.", 4, "Muito bom, mas demora 40min. Vale a pena.", "2025-04-03", null, null, null, null)
        ),
        34 to listOf(
            AvaliacaoDto(138, 14, "Gustavo M.", 5, "Melhor burger artesanal do Chiado!", "2025-04-10", null, null, null, null),
            AvaliacaoDto(139, 11, "Isabel C.", 4, "Queijo de cabra combina muito bem. Top.", "2025-04-04", null, null, null, null)
        ),
        36 to listOf(
            AvaliacaoDto(140, 10, "Teresa R.", 5, "Sashimi de altíssima qualidade. Peixe fresquíssimo!", "2025-04-12", null, null, null, null),
            AvaliacaoDto(141, 15, "André P.", 5, "Melhor sashimi de Lisboa. Sem dúvida.", "2025-04-08", null, null, null, null)
        ),
        38 to listOf(
            AvaliacaoDto(142, 12, "Filipa D.", 5, "Cacio e Pepe autêntico! Como em Roma.", "2025-04-11", null, null, null, null),
            AvaliacaoDto(143, 13, "Miguel S.", 4, "Pasta al dente perfeita. Muito bom.", "2025-04-06", null, null, null, null)
        ),
        40 to listOf(
            AvaliacaoDto(144, 14, "Clara R.", 4, "Muito bom pastel de nata! Não é Belém mas quase.", "2025-04-10", null, null, null, null),
            AvaliacaoDto(145, 10, "Nuno B.", 4, "Massa crocante e creme perfeito. Recomendo.", "2025-04-07", null, null, null, null)
        )
    ).mapValues { (_, v) -> v.toMutableList() }.toMutableMap()

    fun getAvaliacoes(pratoId: Int): List<AvaliacaoDto> = _avaliacoes[pratoId] ?: emptyList()

    private var _nextAvaliacaoId = 200

    fun addAvaliacao(pratoId: Int, userId: Int, classificacao: Int, comentario: String): AvaliacaoDto {
        val av = AvaliacaoDto(
            idAvaliacao = _nextAvaliacaoId++,
            userId = userId,
            autorNome = "Tu",
            classificacao = classificacao,
            comentario = comentario,
            createdAt = "Agora",
            updatedAt = null,
            idResposta = null,
            respostaTexto = null,
            respostaCreatedAt = null
        )
        _avaliacoes.getOrPut(pratoId) { mutableListOf() }.add(0, av)
        return av
    }

    fun deleteAvaliacao(avaliacaoId: Int) {
        _avaliacoes.values.forEach { list ->
            list.removeAll { it.idAvaliacao == avaliacaoId }
        }
    }

    // ─── Zonas disponíveis para filtros ─────────────────────────────────

    val cidades = listOf("Todas", "Lisboa", "Porto", "Coimbra")

    val zonasPorCidade = mapOf(
        "Lisboa" to listOf("Todas", "Alfama", "Avenida", "Baixa", "Bairro Alto", "Belém", "Cais do Sodré", "Chiado", "Intendente", "Príncipe Real", "Restauradores", "Terreiro do Paço"),
        "Porto" to listOf("Todas", "Bolhão", "Campanhã", "Cedofeita", "Foz", "Paranhos", "Ribeira"),
        "Coimbra" to listOf("Todas", "Alta"),
        "Amadora" to listOf("Todas", "Centro")
    )

    // ─── Faixas de preço para filtros ───────────────────────────────────

    data class PriceRange(val label: String, val min: Double, val max: Double)

    val priceRanges = listOf(
        PriceRange("Todos", 0.0, 999.0),
        PriceRange("< 5€", 0.0, 5.0),
        PriceRange("5€ - 10€", 5.0, 10.0),
        PriceRange("10€ - 15€", 10.0, 15.0),
        PriceRange("> 15€", 15.0, 999.0)
    )

    // ─── Algoritmo de filtragem multi-critério ──────────────────────────

    /**
     * Filtra pratos com base em múltiplos critérios simultâneos.
     * Aplica interseção lógica: TODOS os filtros ativos devem ser satisfeitos.
     *
     * @param categoria Categoria selecionada (null ou "Todos" = sem filtro)
     * @param cidade Cidade selecionada (null ou "Todas" = sem filtro)
     * @param zona Zona dentro da cidade (null ou "Todas" = sem filtro)
     * @param priceRange Faixa de preço (null = sem filtro)
     * @param searchQuery Texto de pesquisa livre (nome do prato ou restaurante)
     * @param minRating Rating mínimo (0 = sem filtro)
     * @return Lista filtrada e ordenada por rating descendente
     */
    fun filterPratos(
        categoria: String? = null,
        cidade: String? = null,
        zona: String? = null,
        priceRange: PriceRange? = null,
        searchQuery: String? = null,
        minRating: Double = 0.0,
        source: List<PratoDto>? = null  // null = usa mock interno; não-null = filtra a lista fornecida
    ): List<PratoDto> {
        val sourceList = source ?: pratos
        return sourceList.filter { prato ->
            // Lookup da cidade/zona pelo restauranteId (funciona para IDs reais que coincidam com o mock)
            val rest = restaurantes.find { it.id == prato.restauranteId }

            // Filtro de categoria
            val matchCategoria = categoria.isNullOrBlank() || categoria == "Todos" ||
                    prato.categoria.equals(categoria, ignoreCase = true)

            // Filtro de cidade — usa lookup mock; se não encontrar restaurante, não filtra por cidade
            val matchCidade = cidade.isNullOrBlank() || cidade == "Todas" ||
                    rest?.cidade.equals(cidade, ignoreCase = true) == true ||
                    (rest == null)  // restaurante real não catalogado no mock → passa filtro

            // Filtro de zona
            val matchZona = zona.isNullOrBlank() || zona == "Todas" ||
                    rest?.zona.equals(zona, ignoreCase = true) == true ||
                    (rest == null)

            // Filtro de preço
            val matchPreco = priceRange == null || priceRange.label == "Todos" ||
                    (prato.preco != null && prato.preco >= priceRange.min && prato.preco < priceRange.max)

            // Filtro de pesquisa textual (nome do prato, restaurante ou tipo)
            val query = searchQuery?.trim()?.lowercase()
            val tipo = pratoTipos[prato.id]?.lowercase() ?: ""
            val matchSearch = query.isNullOrBlank() ||
                    prato.nome.lowercase().contains(query) ||
                    prato.restauranteNome.lowercase().contains(query) ||
                    (prato.descricao?.lowercase()?.contains(query) == true) ||
                    tipo.contains(query)

            // Filtro de rating mínimo
            val matchRating = prato.ratingMedio >= minRating

            // Interseção: TODOS os filtros devem passar
            matchCategoria && matchCidade && matchZona && matchPreco && matchSearch && matchRating
        }.sortedByDescending { it.ratingMedio }
    }

    // ─── Pesquisa por Proximidade (Haversine) ───────────────────────────

    fun getNearbyPratos(
        lat: Double,
        lng: Double,
        radiusKm: Double,
        categoria: String? = null
    ): List<PratoDto> {
        return pratos.filter { prato ->
            val rest = restaurantes.find { it.id == prato.restauranteId } ?: return@filter false
            
            // Haversine distance
            val dLat = Math.toRadians(rest.latitude - lat)
            val dLng = Math.toRadians(rest.longitude - lng)
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(rest.latitude)) *
                    Math.sin(dLng / 2) * Math.sin(dLng / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val distKm = 6371 * c
            
            val matchCategoria = categoria == null || prato.categoria.equals(categoria, ignoreCase = true)
            
            distKm <= radiusKm && matchCategoria
        }.sortedByDescending { it.ratingMedio }
    }

    // ─── Sugestões de pesquisa ──────────────────────────────────────────

    /**
     * Retorna sugestões de pesquisa baseadas no input do utilizador.
     * Deduplicadas por tipo de prato, limitadas a 5 resultados.
     */
    fun getSearchSuggestions(query: String, cidade: String = "Todas"): List<PratoDto> {
        if (query.trim().length < 2) return emptyList()
        val q = query.trim().lowercase()
        val matching = pratos.filter {
            val rest = restaurantes.find { r -> r.id == it.restauranteId }
            val matchCidade = cidade == "Todas" || rest?.cidade.equals(cidade, ignoreCase = true)
            matchCidade && (
                it.nome.lowercase().contains(q) ||
                it.restauranteNome.lowercase().contains(q) ||
                (pratoTipos[it.id]?.lowercase()?.contains(q) == true)
            )
        }
        return deduplicatePratos(matching).take(5)
    }

    // ─── Mock de login ──────────────────────────────────────────────────

    fun mockLogin(email: String, password: String): LoginResponse {
        return when {
            email == "goncalo@teste.com" && password == "123456" -> LoginResponse(
                ok = true,
                token = "mock_jwt_token_goncalo_2025",
                user = UserDto(1, "Gonçalo", "cliente", emptyList()),
                error = null,
                csrf_token = "mock_csrf_token"
            )
            email == "restaurante@teste.com" && password == "123456" -> LoginResponse(
                ok = true,
                token = "mock_jwt_token_rest_2025",
                user = UserDto(2, "Chef Manuel", "restaurante", listOf(3, 7)),
                error = null,
                csrf_token = "mock_csrf_token"
            )
            email == "resto@teste.com" && password == "123456" -> LoginResponse(
                ok = true,
                token = "mock_jwt_token_rest_2025",
                user = UserDto(2, "Chef Manuel", "restaurante", listOf(3, 7)),
                error = null,
                csrf_token = "mock_csrf_token"
            )
            email.isBlank() || password.isBlank() -> LoginResponse(
                ok = false, token = null, user = null,
                error = "Email e password são obrigatórios"
            )
            else -> LoginResponse(
                ok = false, token = null, user = null,
                error = "Credenciais inválidas"
            )
        }
    }

    // ─── Mock de avaliações do utilizador ────────────────────────────────

    fun getMyReviews(userId: Int): List<MyReviewItemDto> {
        val result = mutableListOf<MyReviewItemDto>()
        _avaliacoes.forEach { (pratoId, reviews) ->
            reviews.filter { it.userId == userId }.forEach { review ->
                val prato = pratos.find { it.id == pratoId }
                if (prato != null) {
                    result.add(
                        MyReviewItemDto(
                            idAvaliacao = review.idAvaliacao,
                            pratoId = pratoId,
                            pratoNome = prato.nome,
                            pratoImagemUrl = prato.imagemUrl,
                            restauranteNome = prato.restauranteNome,
                            classificacao = review.classificacao,
                            comentario = review.comentario,
                            createdAt = review.createdAt
                        )
                    )
                }
            }
        }
        return result.sortedByDescending { it.createdAt }
    }

    // ─── CRUD de pratos (mock) ───────────────────────────────────────────

    private var _nextPratoId = 100

    fun getRestauranteNome(id: Int): String =
        restaurantes.find { it.id == id }?.nome ?: "Restaurante #$id"

    fun createPrato(
        restauranteId: Int, nome: String, descricao: String?,
        categoria: String?, preco: Double?, imagemUrl: String?
    ): PratoDto {
        val rest = restaurantes.find { it.id == restauranteId }
        val dto = PratoDto(
            id = _nextPratoId++, nome = nome, categoria = categoria,
            descricao = descricao, preco = preco, imagemUrl = imagemUrl,
            restauranteId = restauranteId, restauranteNome = rest?.nome ?: "Restaurante",
            ratingMedio = 0.0, totalAvaliacoes = 0
        )
        _pratos.add(dto)
        return dto
    }

    fun deletePrato(pratoId: Int): Boolean = _pratos.removeAll { it.id == pratoId }

    // ─── Pratos em Destaque ─────────────────────────────────────────────

    fun getDestacados(): List<PratoDto> = pratos.filter { it.destacado }

    fun getDestacados(cidade: String): List<PratoDto> {
        if (cidade == "Todas" || cidade.isBlank()) return getDestacados()
        return pratos.filter { prato ->
            prato.destacado && restaurantes.find { it.id == prato.restauranteId }?.cidade.equals(cidade, ignoreCase = true)
        }
    }

    fun toggleDestacado(pratoId: Int): Boolean {
        val idx = _pratos.indexOfFirst { it.id == pratoId }
        if (idx < 0) return false
        _pratos[idx] = _pratos[idx].copy(destacado = !_pratos[idx].destacado)
        return true
    }

    fun isDestacado(pratoId: Int): Boolean = _pratos.find { it.id == pratoId }?.destacado == true
}

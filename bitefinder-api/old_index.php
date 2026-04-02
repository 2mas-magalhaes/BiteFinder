<?php
?><!DOCTYPE html>
<html lang="pt">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>BiteFinder Platform</title>
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
	<link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&family=Sora:wght@600;700;800&display=swap" rel="stylesheet" />
	<style>
		:root {
			--brand: #0f172a;
			--accent: #2563eb;
			--bg: #edf2f9;
			--surface: #ffffff;
			--text: #0b1222;
			--text-soft: #5f6f89;
			--border: rgba(15, 23, 42, 0.05);
			--shadow: 0 24px 48px -12px rgba(15, 23, 42, 0.08);
			--shadow-soft: 0 12px 36px -12px rgba(15, 23, 42, 0.06);
		}

		* { box-sizing: border-box; }

		body {
			margin: 0;
			font-family: 'Manrope', 'Segoe UI', sans-serif;
			background:
				radial-gradient(circle at 12% 0%, #dbeafe 0%, rgba(219, 234, 254, 0) 42%),
				radial-gradient(circle at 88% 0%, #c7d2fe 0%, rgba(199, 210, 254, 0) 35%),
				linear-gradient(180deg, #f3f7fd 0%, var(--bg) 100%);
			color: var(--text);
			min-height: 100vh;
		}

		main {
			max-width: 1100px;
			margin: 0 auto;
			padding: 40px 24px 80px;
		}

		.topbar {
			display: flex;
			justify-content: space-between;
			align-items: center;
			gap: 10px;
			margin-bottom: 30px;
			flex-wrap: wrap;
		}

		.brand {
			display: flex;
			align-items: center;
			gap: 10px;
			font-family: 'Sora', 'Manrope', sans-serif;
			font-weight: 800;
			letter-spacing: -0.02em;
		}

		.brand img {
			width: 40px;
			height: 40px;
			border-radius: 10px;
			border: 1px solid rgba(148, 163, 184, 0.35);
		}

		.hero {
			background: var(--surface);
			border: 1px solid var(--border);
			border-radius: 40px;
			padding: clamp(40px, 8vw, 80px);
			box-shadow: var(--shadow);
			margin-bottom: 40px;
		}

		.pill {
			display: inline-flex;
			border-radius: 999px;
			background: var(--bg);
			color: var(--accent);
			font-weight: 800;
			font-size: 0.85rem;
			padding: 8px 16px;
			text-transform: uppercase;
			letter-spacing: 0.1em;
			margin-bottom: 24px;
		}

		h1 {
			margin: 0 0 24px;
			font-family: 'Sora', 'Manrope', sans-serif;
			font-size: clamp(2.5rem, 6vw, 4.5rem);
			font-weight: 800;
			line-height: 1.1;
			letter-spacing: -0.04em;
			color: var(--brand);
			max-width: 800px;
		}

		.hero p {
			margin: 0;
			color: var(--text-soft);
			font-size: 1.25rem;
			max-width: 700px;
			line-height: 1.6;
		}

		.cta-row {
			display: flex;
			gap: 16px;
			flex-wrap: wrap;
			margin-top: 32px;
		}

		.btn {
			text-decoration: none;
			font-weight: 800;
			border-radius: 999px;
			padding: 16px 28px;
			display: inline-flex;
			align-items: center;
			justify-content: center;
			transition: all .2s cubic-bezier(0.16, 1, 0.3, 1);
			font-size: 1.05rem;
			box-shadow: var(--shadow-soft);
		}

		.btn.primary {
			background: var(--brand);
			color: #fff;
		}

		.btn.secondary {
			background: var(--surface);
			color: var(--brand);
		}

		.btn:hover { 
			transform: scale(1.02);
			box-shadow: var(--shadow);
		}

		.grid {
			display: grid;
			grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
			gap: 24px;
		}

		.card {
			background: var(--surface);
			border: 1px solid var(--border);
			border-radius: 24px;
			padding: 32px 24px;
			box-shadow: var(--shadow-soft);
			transition: transform 0.2s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.2s;
		}
		
		.card:hover {
		    transform: scale(1.01);
		    box-shadow: var(--shadow);
		}

		.card h3 {
			margin: 0 0 12px;
			font-family: 'Sora', 'Manrope', sans-serif;
			font-size: 1.25rem;
			font-weight: 800;
			color: var(--brand);
		}

		.card p {
			margin: 0;
			color: var(--text-soft);
			font-size: 1.05rem;
			line-height: 1.6;
		}
	</style>
</head>
<body>
	<main>
		<header class="topbar">
			<div class="brand">
				<img src="https://bitefinderstorage.blob.core.windows.net/icons/logo-bf.png" alt="BiteFinder" />
				<span>BiteFinder Platform</span>
			</div>
			<a class="btn secondary" href="restaurant_portal.html">Portal Parceiro</a>
		</header>

		<section class="hero">
			<span class="pill">BiteFinder API online</span>
			<h1>Food discovery com performance real para clientes e restaurantes.</h1>
			<p>A plataforma BiteFinder está ativa: autenticação, catálogo de pratos, avaliações e operações de parceiros prontas para produção.</p>
			<div class="cta-row">
				<a class="btn primary" href="restaurant_portal.html">Abrir Portal de Restaurante</a>
				<a class="btn secondary" href="api/test_db.php">Testar Conexão API</a>
			</div>
		</section>

		<section class="grid">
			<article class="card">
				<h3>Autenticação JWT</h3>
				<p>Login seguro para utilizadores e parceiros com proteção por token.</p>
			</article>
			<article class="card">
				<h3>Gestão de Menu</h3>
				<p>Criação, edição e remoção de pratos com atualização em tempo real.</p>
			</article>
			<article class="card">
				<h3>Avaliações e Respostas</h3>
				<p>Fluxo completo de reputação com feedback de clientes e resposta do restaurante.</p>
			</article>
			<article class="card">
				<h3>Pronto para Mobile</h3>
				<p>API alinhada com app Android ByteFinder para experiência unificada.</p>
			</article>
		</section>
	</main>
</body>
</html>

<?php
?><!DOCTYPE html>
<html lang="pt">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>BiteFinder</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&family=Sora:wght@600;700;800&display=swap" rel="stylesheet" />
  <style>
    :root {
      --brand: #1B6EC2;
      --brand-deep: #103E7D;
      --brand-bright: #5AA7F2;
      --navy-900: #061121;
      --navy-800: #0A1628;
      --navy-700: #102844;
      --surface: #F7FAFF;
      --surface-soft: #EBF3FF;
      --text: #F4F8FF;
      --ink: #12213D;
      --muted: #9FB2CC;
      --border: rgba(214,232,248,0.16);
      --shadow: 0 34px 90px -42px rgba(0,0,0,0.68);
      --ease: cubic-bezier(.2,.8,.2,1);
      font-family: 'Manrope', 'Segoe UI', sans-serif;
    }

    * { box-sizing: border-box; }

    body {
      margin: 0;
      min-height: 100vh;
      background: linear-gradient(140deg, var(--navy-900), var(--navy-800) 48%, var(--navy-700));
      color: var(--text);
    }

    a { color: inherit; }

    .hero {
      min-height: 92vh;
      display: flex;
      flex-direction: column;
      background:
        linear-gradient(90deg, rgba(6,17,33,0.94) 0%, rgba(6,17,33,0.74) 48%, rgba(6,17,33,0.22) 100%),
        url('https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=2200&q=85') center/cover;
    }

    .nav {
      width: min(1180px, calc(100% - 40px));
      margin: 0 auto;
      padding: 26px 0;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 20px;
    }

    .brand {
      display: flex;
      align-items: center;
      gap: 12px;
      font-family: 'Sora', 'Manrope', sans-serif;
      font-weight: 800;
      font-size: 1.05rem;
    }

    .brand img {
      width: 42px;
      height: 42px;
      border-radius: 12px;
      border: 1px solid var(--border);
      box-shadow: 0 14px 28px -20px rgba(90,167,242,0.8);
    }

    .nav-actions {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
      justify-content: flex-end;
    }

    .btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      min-height: 46px;
      padding: 0 18px;
      border-radius: 999px;
      text-decoration: none;
      font-weight: 800;
      transition: transform .18s var(--ease), box-shadow .18s var(--ease), background .18s var(--ease);
    }

    .btn.primary {
      background: linear-gradient(135deg, var(--brand-bright), var(--brand));
      color: #fff;
      box-shadow: 0 22px 42px -26px rgba(90,167,242,0.95);
    }

    .btn.secondary {
      background: rgba(247,250,255,0.1);
      border: 1px solid var(--border);
      color: var(--text);
      backdrop-filter: blur(12px);
    }

    .btn:hover {
      transform: translateY(-1px);
      box-shadow: 0 26px 52px -28px rgba(90,167,242,1);
    }

    .hero-inner {
      width: min(1180px, calc(100% - 40px));
      margin: auto;
      padding: 42px 0 76px;
    }

    h1 {
      font-family: 'Sora', 'Manrope', sans-serif;
      font-size: clamp(3rem, 7vw, 6.4rem);
      line-height: 0.98;
      margin: 0;
      max-width: 820px;
      letter-spacing: 0;
    }

    .lead {
      margin: 26px 0 0;
      max-width: 640px;
      color: rgba(244,248,255,0.78);
      font-size: clamp(1.05rem, 2vw, 1.35rem);
      line-height: 1.65;
    }

    .hero-actions {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      margin-top: 34px;
    }

    .preview {
      width: min(1180px, calc(100% - 40px));
      margin: -54px auto 0;
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      position: relative;
      z-index: 2;
    }

    .card {
      background: rgba(247,250,255,0.94);
      color: var(--ink);
      border: 1px solid rgba(214,232,248,0.7);
      border-radius: 24px;
      padding: 22px;
      min-height: 168px;
      box-shadow: var(--shadow);
      backdrop-filter: blur(14px);
      transition: transform .22s var(--ease), box-shadow .22s var(--ease);
    }

    .card:hover {
      transform: translateY(-4px);
      box-shadow: 0 38px 94px -44px rgba(27,110,194,0.78);
    }

    .metric {
      color: var(--brand);
      font-size: 2rem;
      font-weight: 800;
      margin: 0 0 12px;
      font-family: 'Sora', 'Manrope', sans-serif;
    }

    .card h3 {
      margin: 0 0 8px;
      font-size: 1rem;
      font-family: 'Sora', 'Manrope', sans-serif;
    }

    .card p {
      margin: 0;
      color: #5F7190;
      line-height: 1.55;
      font-size: 0.94rem;
    }

    .section {
      width: min(1180px, calc(100% - 40px));
      margin: 0 auto;
      padding: 92px 0;
    }

    .section h2 {
      font-family: 'Sora', 'Manrope', sans-serif;
      letter-spacing: 0;
      font-size: clamp(2rem, 4vw, 3.4rem);
      line-height: 1.08;
      margin: 0 0 16px;
    }

    .section p {
      max-width: 720px;
      color: var(--muted);
      font-size: 1.08rem;
      line-height: 1.7;
      margin: 0;
    }

    @media (max-width: 900px) {
      .preview { grid-template-columns: repeat(2, 1fr); }
      .hero { min-height: 86vh; }
    }

    @media (max-width: 560px) {
      .nav { align-items: flex-start; }
      .preview { grid-template-columns: 1fr; }
      .hero-actions, .nav-actions { width: 100%; }
      .btn { width: 100%; }
    }
  </style>
</head>
<body>
  <header class="hero">
    <nav class="nav">
      <div class="brand">
        <img src="https://bitefinderstorage.blob.core.windows.net/icons/logo-bf.png" alt="BiteFinder" />
        <span>BiteFinder</span>
      </div>
      <div class="nav-actions">
        <a class="btn secondary" href="api/test_db.php">API status</a>
        <a class="btn primary" href="index.html">Portal restaurante</a>
      </div>
    </nav>

    <div class="hero-inner">
      <h1>Descoberta inteligente de pratos.</h1>
      <p class="lead">BiteFinder ajuda clientes a escolher melhor e dá aos restaurantes um portal premium para gerir pratos, reviews, destaques e performance.</p>
      <div class="hero-actions">
        <a class="btn primary" href="index.html">Abrir portal</a>
        <a class="btn secondary" href="api/pratos/list.php">Ver endpoints</a>
      </div>
    </div>
  </header>

  <section class="preview" aria-label="Capacidades BiteFinder">
    <article class="card">
      <p class="metric">4.8</p>
      <h3>Reviews por prato</h3>
      <p>Feedback granular para decisões mais confiantes.</p>
    </article>
    <article class="card">
      <p class="metric">2.1km</p>
      <h3>Discovery local</h3>
      <p>Pratos próximos com contexto de distância e qualidade.</p>
    </article>
    <article class="card">
      <p class="metric">15€</p>
      <h3>Destaques premium</h3>
      <p>Promoção de pratos com presença visual forte.</p>
    </article>
    <article class="card">
      <p class="metric">JWT</p>
      <h3>Portal seguro</h3>
      <p>Autenticação e gestão operacional para parceiros.</p>
    </article>
  </section>

  <main class="section">
    <h2>Um ecossistema azul, coeso e pronto para escala.</h2>
    <p>A app Android, o site público e o portal de restaurante passam a partilhar a mesma linguagem: navy profundo, CTAs sapphire, superfícies limpas, cards de prato visuais e motion contido.</p>
  </main>
</body>
</html>

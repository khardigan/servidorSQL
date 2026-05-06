# pip install playwright
# playwright install
import asyncio
from playwright.async_api import async_playwright

CATEGORIAS = [
    "https://www.dia.es/frutas/c/L105",
    "https://www.dia.es/verduras/c/L104",
    "https://www.dia.es/carnes/c/L102",
    "https://www.dia.es/pescados-y-mariscos/c/L103",
    "https://www.dia.es/panaderia/c/L112",
    "https://www.dia.es/yogures-y-postres/c/L113",
    "https://www.dia.es/congelados/c/L119",
    "https://www.dia.es/arroz-pastas-y-legumbres/c/L106"
]

POR_CATEGORIA = 20


# 🔥 handler para interceptar respuestas
async def log_response(response):
    url = response.url
    if "product" in url or "search" in url:
        try:
            print("🔥 API DETECTADA:", url)
        except:
            pass


async def scrapear_categoria(browser, url):
    page = await browser.new_page()

    # 🔥 aquí enganchas el listener
    page.on("response", lambda response: asyncio.create_task(log_response(response)))

    await page.goto(url)
    await page.wait_for_selector('[data-test-id="product-card"]')

    productos = []

    while len(productos) < POR_CATEGORIA:
        items = await page.query_selector_all('[data-test-id="product-card"]')

        for item in items:
            nombre_el = await item.query_selector('[data-test-id="search-product-card-name"]')
            precio_el = await item.query_selector('[data-test-id="search-product-card-unit-price"]')
            img_el = await item.query_selector('[data-test-id="search-product-card-image"]')
            link_el = await item.query_selector('a[href*="/p/"]')

            nombre = await nombre_el.inner_text() if nombre_el else None
            precio = await precio_el.inner_text() if precio_el else None
            imagen = await img_el.get_attribute("src") if img_el else None
            link = await link_el.get_attribute("href") if link_el else None

            if imagen and imagen.startswith("/"):
                imagen = "https://www.dia.es" + imagen

            if link and link.startswith("/"):
                link = "https://www.dia.es" + link

            producto = (nombre, precio, imagen, link)

            if producto not in productos:
                productos.append(producto)

        # scroll
        await page.mouse.wheel(0, 3000)
        await page.wait_for_timeout(1500)

        if len(productos) >= POR_CATEGORIA:
            break

    await page.close()
    return productos


async def main():
    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)

        tareas = [scrapear_categoria(browser, url) for url in CATEGORIAS]
        resultados = await asyncio.gather(*tareas)

        await browser.close()

    productos_unicos = set()
    for lista in resultados:
        for prod in lista:
            productos_unicos.add(prod)

    productos_final = [
        {
            "nombre": p[0],
            "precio": p[1],
            "imagen": p[2],
            "url": p[3]
        }
        for p in productos_unicos
    ]

    print(f"\n🔥 TOTAL PRODUCTOS ÚNICOS: {len(productos_final)}\n")

    for p in productos_final:
        print(p)


asyncio.run(main())
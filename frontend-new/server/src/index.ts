import path from "path";
import express, { Request, Response } from "express";
import compression from "compression";

const dist = `../../dist`;

// This contains a list of static routes (assets)
// eslint-disable-next-line @typescript-eslint/no-var-requires
const { ssr } = require(`${dist}/server/package.json`);

// The manifest is required for preloading assets
// eslint-disable-next-line @typescript-eslint/no-var-requires
const manifest = require(`${dist}/client/ssr-manifest.json`);

// This is the server renderer we just built
// eslint-disable-next-line @typescript-eslint/no-var-requires
const { default: renderPage } = require(`${dist}/server`);

const server = express();

// gzip is cool
server.use(compression());

// Serve every static asset route
for (const asset of ssr.assets || []) {
  server.use("/" + asset, express.static(path.join(__dirname, `${dist}/client/` + asset)));
}

// main
server.get("*", async (request: Request, response: Response) => {
  const url = request.protocol + "://" + request.get("host") + request.originalUrl;

  const { html, status, statusText, headers } = await renderPage(url, {
    manifest,
    preload: true,
    request,
    response,
  });

  response.contentType("text/html");
  response.writeHead(status || 200, statusText || headers, headers);
  response.end(html);
});

const port = 1337;
console.log(`Server started: http://localhost:${port}`);
server.listen(port);

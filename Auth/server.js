'use strict';

const Hapi = require('@hapi/hapi');
const routes = require('./routes'); // Import file routes.js

const init = async () => {
    const server = Hapi.server({
        port: process.env.PORT || 8080, // Gunakan port yang tersedia atau default 8080
        host: '0.0.0.0' // Mendengarkan permintaan dari luar
    });

    // Register routes from routes.js
    server.route(routes);

    await server.start();
    console.log('Server running on %s', server.info.uri);
};

process.on('unhandledRejection', (err) => {
    console.log(err);
    process.exit(1);
});

init();

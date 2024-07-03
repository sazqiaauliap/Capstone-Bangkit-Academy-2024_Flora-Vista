const Joi = require('@hapi/joi');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const mysql = require('mysql2/promise');
require('dotenv').config();

// Konfigurasi koneksi ke database
const db = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME
});

// Middleware untuk memverifikasi JWT
const validateToken = async (decoded, request, h) => {
    try {
        const [rows] = await db.execute('SELECT * FROM users WHERE id = ?', [decoded.id]);
        if (rows.length > 0) {
            return { isValid: true };
        } else {
            return { isValid: false };
        }
    } catch (err) {
        console.error(err);
        return { isValid: false };
    }
};

// Definisi rute untuk register
const registerRoute = {
    method: 'POST',
    path: '/register',
    options: {
        validate: {
            payload: Joi.object({
                name: Joi.string().required(),
                email: Joi.string().email().required(),
                password: Joi.string().min(6).required()
            })
        }
    },
    handler: async (request, h) => {
        const { name, email, password } = request.payload;

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);

        // Simpan user ke database
        try {
            const [result] = await db.execute(
                'INSERT INTO users (name, email, password) VALUES (?, ?, ?)',
                [name, email, hashedPassword]
            );
            return h.response({ message: 'User registered successfully' }).code(201);
        } catch (err) {
            console.error(err);
            return h.response({ message: 'Error registering user' }).code(500);
        }
    }
};

// Definisi rute untuk login
const loginRoute = {
    method: 'POST',
    path: '/login',
    options: {
        validate: {
            payload: Joi.object({
                email: Joi.string().email().required(),
                password: Joi.string().required()
            })
        }
    },
    handler: async (request, h) => {
        const { email, password } = request.payload;

        // Cari user di database
        try {
            const [rows] = await db.execute(
                'SELECT * FROM users WHERE email = ?',
                [email]
            );

            if (rows.length === 0) {
                return h.response({ message: 'Invalid email or password' }).code(401);
            }

            const user = rows[0];

            // Bandingkan password
            const isValid = await bcrypt.compare(password, user.password);

            if (!isValid) {
                return h.response({ message: 'Invalid email or password' }).code(401);
            }

            // Buat token JWT
            const token = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET, {
                expiresIn: '1h'
            });

            // Hapus password dari respons
            delete user.password;

            return h.response({ user, message: 'Login successful', token }).code(200);
        } catch (err) {
            console.error(err);
            return h.response({ message: 'Error logging in user' }).code(500);
        }
    }
};

// Definisi rute untuk mengambil history user
const historyRoute = {
    method: 'GET',
    path: '/history/{userId}',
    options: {
        validate: {
            params: Joi.object({
                userId: Joi.number().integer().required()
            }),
            headers: Joi.object({
                'authorization': Joi.string().required()
            }).unknown()
        }
    },
    handler: async (request, h) => {
        const { userId } = request.params;
        const token = request.headers.authorization.split(' ')[1];

        try {
            const decoded = jwt.verify(token, process.env.JWT_SECRET);
            if (decoded.id !== parseInt(userId, 10)) {
                return h.response({ message: 'Unauthorized' }).code(401);
            }

            const [rows] = await db.execute(
                'SELECT id, user_id, link_gambar, tanggal, hasil_prediksi FROM history WHERE user_id = ?',
                [userId]
            );

            return h.response(rows).code(200);
        } catch (err) {
            console.error(err);
            return h.response({ message: 'Error retrieving user history' }).code(500);
        }
    }
};

module.exports = [registerRoute, loginRoute, historyRoute];

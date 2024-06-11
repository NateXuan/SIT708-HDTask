const express = require('express');
const connectDB = require('./config/dbConnection');
const userRoutes = require('./routes/userRoutes');
const userInformationRoutes = require('./routes/userInformationRoutes');
require('dotenv').config();

const app = express();

app.use(express.json());

connectDB();

app.use('/', userRoutes);
app.use('/', userInformationRoutes);

app.get('/', (req, res) => {
    res.send('Welcome to the Healthy Guide App API!');
});

const PORT = process.env.PORT;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on port ${PORT}`);
});
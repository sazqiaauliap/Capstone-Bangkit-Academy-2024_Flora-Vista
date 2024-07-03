const express = require('express');
const dotenv = require('dotenv');
const plantTipsRoutes = require('./routes/plantTipsRoutes');

// Load environment variables
dotenv.config();

const app = express();
const port = process.env.PORT || 8080;

app.use(express.json());

// Routes
app.use('/guides', plantTipsRoutes);

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});

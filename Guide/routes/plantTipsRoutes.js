const express = require('express');
const { getPlantTips } = require('../controllers/plantTipsController');

const router = express.Router();

router.get('/', getPlantTips);

module.exports = router;

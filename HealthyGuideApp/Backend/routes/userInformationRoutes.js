const express = require('express');
const router = express.Router();
const userInformationController = require('../controllers/userInformationController');

router.post('/user_information', userInformationController.saveUserInformation);
router.put('/user_information', userInformationController.updateUserInformation);
router.post('/health_plan', userInformationController.generateHealthPlan);
router.post('/today_plan', userInformationController.generateTodayPlan);

module.exports = router;

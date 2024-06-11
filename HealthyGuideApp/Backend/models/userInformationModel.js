const mongoose = require('mongoose');

const userInformationSchema = new mongoose.Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    age: { type: Number, required: true },
    gender: { type: String, required: true },
    height: { type: Number, required: true },
    weight: { type: Number, required: true },
    healthCondition: { type: String, required: true },
    goal: { type: String, required: true },
    dietaryPreferences: { type: String, required: true }
});

const UserInformation = mongoose.model('UserInformation', userInformationSchema);

module.exports = UserInformation;

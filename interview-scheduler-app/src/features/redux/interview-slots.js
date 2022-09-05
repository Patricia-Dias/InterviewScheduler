
// types
export const NAME = `@interviewSlotsData`;
export const APPEND_SLOT = `${NAME}/APPEND_SLOT`;
export const INSERT_ITEMS = `${NAME}/INSERT_ITEMS`;
export const REMOVE_SLOTS = `${NAME}/REMOVE_SLOTS`;

// initialization
const initialState = {
  isInitiallyLoaded: false,
  interviewSlots: [],
  error: '',
};



function insertItems(array, action) {
  let newArray = array.slice()
  newArray.splice(action.index, 0, action.item)
  return newArray
}


export default function slotsReducer(state = initialState, action = {}) {
  switch (action.type) {
  case APPEND_SLOT:
    return {
      ...state,
      interviewSlots: [
        ...state.interviewSlots,
        action.item,
      ]
    };
  case INSERT_ITEMS:
    return {
      ...state,
      interviewSlots: insertItems(state.interviewSlots, action)
    };
  case REMOVE_SLOTS:
    return {
      ...state,
      interviewSlots: []
    };
  default:
     return state;
  }
}
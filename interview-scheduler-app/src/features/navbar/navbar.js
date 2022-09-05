import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import { useNavigate } from 'react-router-dom';
import { appColors } from '../../app/AppConstants';
import { useDispatch } from 'react-redux';
import slotsReducer, { REMOVE_SLOTS } from '../redux/interview-slots';

const RED = appColors['red'];
const GREEN = appColors['green'];
const BEIGE = appColors['beige'];

const pages = {
  Interviewer : '/interviewer/login',
  Candidate : '/candidate/login'
};

// defines the Avatar Icon and the initial letter inside
function stringAvatar(name) {
    return {
      sx: {
        bgcolor: RED,
      },
      children: `${name.split(' ')[0][0]}`.toUpperCase(),
    };
  }

const ResponsiveAppBar = () => {
  const authenticated = JSON.parse(localStorage.getItem('authenticated'));
  const user = JSON.parse(localStorage.getItem('user'));

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [anchorElNav, setAnchorElNav] = React.useState(null);

  const handleOpenNavMenu = (event) => {
    setAnchorElNav(event.currentTarget);
  };
  const handleLogout = (navigateHome = true) => {
    localStorage.setItem('authenticated', JSON.parse(false));
    localStorage.removeItem('user');
    dispatch(slotsReducer({type: REMOVE_SLOTS}));
    if (navigateHome){
      navigate('/');
    }
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  return (
    <AppBar position='fixed' style={{backgroundColor : BEIGE}}>
      <Container maxWidth="xl">
        <Toolbar disableGutters >
          <Typography
            variant="h6"
            noWrap
            component="a"
            href="/"
            sx={{
              mr: 2,
              display: { xs: 'none', md: 'flex' },
              fontFamily: 'monospace',
              fontWeight: 700,
              letterSpacing: '.1rem',
              textDecoration: 'none',
              color: GREEN
            }}
          >
            INTERVIEW SCHEDULER
          </Typography>

          <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleOpenNavMenu}
              sx={{
                color: GREEN
              }}
            >
              <MenuIcon />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorElNav}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'left',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'left',
              }}
              open={Boolean(anchorElNav)}
              onClose={handleCloseNavMenu}
              sx={{
                display: { xs: 'block', md: 'none' },
              }}
            >
              {Object.keys(pages).map((key, index) =>
                <MenuItem key={index} onClick={
                  function(){
                    handleCloseNavMenu(); 
                    if (authenticated){
                      handleLogout(false);
                    }
                    navigate(pages[key]);
                  }}
                 >
                  <Typography textAlign="center">{key}</Typography>
                </MenuItem>
            )}
            </Menu>
          </Box>
          <Typography
            variant="h5"
            noWrap
            component="a"
            href="/"
            sx={{
              mr: 2,
              display: { xs: 'flex', md: 'none' },
              flexGrow: 1,
              fontFamily: 'monospace',
              fontWeight: 700,
              letterSpacing: '.1rem',
              color: GREEN,
              textDecoration: 'none',
            }}
          >
            INTERVIEW SCHEDULER
          </Typography>
          <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
            {Object.keys(pages).map((key, index) =>
              <Button
                  key={index}
                  onClick={()=>{
                    if (authenticated){
                      handleLogout(false);
                    }
                    navigate(pages[key]);
                  }}
                  sx={{ my: 2, color: GREEN, display: 'block' }}
                >
                  {key}
                </Button>
            )}
          </Box>
          {authenticated ? 
          <Box sx={{ flexGrow: 0 }}>
            <Tooltip title="Logout">
              <IconButton onClick={handleLogout} sx={{ p: 0 }}>
                <Avatar {...stringAvatar(user.name)} />
              </IconButton>
            </Tooltip>
          </Box> : null }
        </Toolbar>
      </Container>
    </AppBar>
  );
};
export default ResponsiveAppBar;
